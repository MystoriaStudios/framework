package net.revive.framework.deployment

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.DockerClientConfig
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp
import net.revive.framework.allocation.AllocationService
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.deployment.template.DeploymentTemplate
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Service
object DeploymentService {

    // Eager docker configuration on a locally hosted instance
    private val dockerConfig: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("tcp://localhost:2375")
        .build()

    val dockerClient: DockerClient = DockerClientBuilder.getInstance(dockerConfig).build()

    val templates: MutableMap<String, DeploymentTemplate> = mutableMapOf()

    @Configure
    fun configure() {
        FrameworkApp.use { app ->
            val tempDirectory = File("containers")

            if (!tempDirectory.exists()) {
                tempDirectory.mkdir()
            }

            val peristDirectory = File("persistent")

            if (!peristDirectory.exists()) {
                peristDirectory.mkdir()
            }

            val directory = File(app.getBaseFolder(), "templates")

            if (!directory.exists()) {
                directory.mkdir()
            }

            directory.listFiles().filter(File::isDirectory).forEach {
                app.load(
                    JsonConfig("templates/${it.name}/template.json"),
                    DeploymentTemplate::class
                ).apply {
                    val template = this as DeploymentTemplate
                    templates[template.templateKey] = template
                }
            }

            if (templates.isEmpty()) {
                templates["example"] = DeploymentTemplate().apply {
                    this.save()
                }
            }
        }

    }

    @Close
    fun close() {
        templates.values.forEach(DeploymentTemplate::save)
        FrameworkApp.use { app ->
            File("containers").delete()
        }
    }

    fun deploy(template: DeploymentTemplate): InspectContainerResponse? {
        val log: (String) -> Unit = { msg ->
            Framework.instance.log("Deployment", msg)
        }
        log("Starting deployment of template ${template.templateKey}")

        val allocation = AllocationService.take() ?: throw RuntimeException("No allocation available.")
        log("Assigned allocation $allocation")


        val directory = File(if (template.persisted) "persistent/${template.templateKey}" else "containers/${template.idScheme.replace("%containerId%", "${allocation.port}")}")
        if (!directory.exists() && template.directory.copyRecursively(directory, true)) {
            log("Copied Template Directory")

            // todo make local cache for server jars to copy from if downloaded once already in the node cache folder mhm @98Ping sirr yees do that
            val url = URL(template.serverExecutableOrigin)
            val connection = url.openConnection()
            connection.connect()

            val inputStream = BufferedInputStream(url.openStream())
            val fileOutput = FileOutputStream(directory)

            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutput.write(buffer, 0, bytesRead)
            }

            fileOutput.close()
            inputStream.close()
            log("Downloaded server jar file")

            val splitOrigin = template.serverExecutableOrigin.split("/")
            var jarFile = splitOrigin[splitOrigin.size - 1]

            var templateContainer: CreateContainerResponse? = null

            try {
                templateContainer = dockerClient.createContainerCmd(template.dockerImage)
                    .withName(if (template.persisted) template.templateKey else template.idScheme.replace("%containerId%", "${allocation.port}"))
                    .withIpv4Address(allocation.bindAddress)
                    .withExposedPorts(
                        ExposedPort.tcp(
                            allocation.port
                        )
                    )
                    .withWorkingDir(FrameworkApp.useWithReturn {
                        directory.absolutePath
                    })

                    .withCmd(template.startupCommand.replace("%originJar%", jarFile))
                    .withAttachStderr(false)
                    .withAttachStdin(false)
                    .withAttachStdout(false)
                    .exec()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            if (templateContainer != null) {
                log("Created container with id ${templateContainer.id}")
            } else {
                log("Error creating container.")
                throw RuntimeException()
            }

            // Start container
            log("Starting container ${templateContainer.id}")
            dockerClient.startContainerCmd(templateContainer.id).exec();

            log("Inspecting container ${templateContainer.id}")
            // Return inspection
            return dockerClient.inspectContainerCmd(templateContainer.id).exec()
        } else {
            // smt is vewy bad mhm, need force kill fr.
        }

        return null
    }
}