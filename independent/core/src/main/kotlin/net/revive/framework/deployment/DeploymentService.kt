package net.revive.framework.deployment

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.BuildImageCmd
import com.github.dockerjava.api.command.BuildImageResultCallback
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.command.InspectImageResponse
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Volume
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.exec.BuildImageCmdExec
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp
import net.revive.framework.allocation.AllocationService
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.constants.Deployment
import net.revive.framework.deployment.cloudflare.CloudflareCredentialService
import net.revive.framework.deployment.cloudflare.CloudflareRequestController
import net.revive.framework.deployment.docker.WrappedDockerContainer
import net.revive.framework.deployment.template.DeploymentTemplate
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import org.apache.http.conn.HttpHostConnectException
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Properties

@Service
object DeploymentService {

    val log: (String) -> Unit = { msg ->
        Framework.instance.log("Deployment", msg)
    }
    private val dockerConfig: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost(File("docker.host").apply {
            if (!this.exists()) this.writeText("tcp://localhost:2375")
        }.readText())
        .build()

    lateinit var cloudflareController: CloudflareRequestController

    val dockerClient: DockerClient = DockerClientBuilder.getInstance(dockerConfig).build()

    val templates: MutableMap<String, DeploymentTemplate> = mutableMapOf()
    val containers: MutableMap<String, WrappedDockerContainer> = mutableMapOf()

    @Configure
    fun configure() {
        FrameworkApp.use { app ->
            if (app.settingsConfig.cloudflare_token.isNotEmpty()) {
                CloudflareCredentialService.withToken(app.settingsConfig.cloudflare_token)
                cloudflareController = CloudflareCredentialService.requestController
                log("Initializing cloudflare connections")

                cloudflareController.listZones().forEach {
                    log("Found zone with name ${it.name}")
                    log("FOUND RECORDS:")
                    cloudflareController.listDNSRecords(it.name).forEach { record ->
                        log(Framework.useWithReturn {
                            it.serializer.serialize(record)
                        })
                    }
                }
            }


            val cacheDirectory = File("cache")
2
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdir()
            }

            val directory = File(app.getBaseFolder(), "templates")

            if (!directory.exists()) {
                directory.mkdir()
            }

            val global = File("global")
            if (!global.exists()) {
                initializeGlobalDirectory()

                File(global, "eula.txt").writeText("eula=true")
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

        runCatching {
            Framework.useWithReturn {
                deploy(templates.values.first())?.let { it1 -> it.serializer.serialize(it1) }
            }?.let { log(it) }
        }.onFailure {
            log("There was an error connecting to your docker instance are you sure you have configured it correctly?")
            log(it.message ?: it::class.simpleName ?: "Please contact support with the error codee #TEMPLATE-NO-THROWABLE-FAILURE")
        }
    }

    fun initializeGlobalDirectory(target: File? = null) {
        val globalDirectory = File("global")
        if (!globalDirectory.exists()) {
            globalDirectory.mkdir()
            log("Created global directory for templates.")
        }

        if (target != null) {
            if (target.exists() && target.isDirectory) {
                globalDirectory.listFiles()?.forEach {
                    it.copyRecursively(File(target, it.name), true)
                } ?: {
                    log("Copied all global files into runtime.")
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
        log("Starting deployment of template ${template.templateKey}")

        val allocation = AllocationService.take() ?: throw RuntimeException("No allocation available.")
        log("Assigned allocation $allocation")

        val directory = File(if (template.persisted) "persistent/${template.templateKey}" else "containers/${template.idScheme.replace("%containerId%", "${allocation.port}")}")
        if (!directory.exists()) {
            directory.mkdirs()
            if (!template.persisted) directory.deleteOnExit()
        }

        if (directory.exists() && template.directory.copyRecursively(directory, true)) {
            log("Copied Template Directory")
            val cacheDirectory = File("cache")
            val jarFileName = template.serverExecutableOrigin.substringAfterLast("/")
            val cachedJarFile = File(cacheDirectory, jarFileName)

            if (cachedJarFile.exists()) {
                Files.copy(cachedJarFile.toPath(), File(directory, jarFileName).toPath(), StandardCopyOption.REPLACE_EXISTING)
                log("Using cached server jar from $cacheDirectory")
            } else {
                val url = URL(template.serverExecutableOrigin)
                val connection = url.openConnection()
                connection.connect()

                val inputStream = BufferedInputStream(url.openStream())
                val fileOutput = FileOutputStream(cachedJarFile)

                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    fileOutput.write(buffer, 0, bytesRead)
                }

                fileOutput.close()
                inputStream.close()
                log("Downloaded server jar file to cache")

                Files.copy(cachedJarFile.toPath(), File(directory, jarFileName).toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            initializeGlobalDirectory(directory)

            log("Parsing Template for environment startup.")

            log("Parsing environment type startup.")
            // implement an environemnt check to see if there on what ever seerveer typee andd then do thee files accordingly mhjm
            File(directory, "server.properties").apply {
                log("Setting up server.properties.")
                val properties = Properties()
                if (this.exists()) properties.load(this.inputStream())

                properties.setProperty("server-ip", allocation.bindAddress)
                properties.setProperty("server-port", allocation.port.toString())
                properties.store(this.outputStream(), null)
            }

            Deployment.Info(
                template.idScheme.replace("%containerId%", "${allocation.port}"),
                template.templateGroups
            ).save(File(directory, "deployment.json"))

            File(directory, "start.sh").apply {
                log("Setting up startup script")
                this.writeText(template.startupCommand.replace("%originJar%", File(directory, jarFileName).name))
            }
            log("Finished Parsing Template proceeding to contact Docker.")

            val image: BuildImageResultCallback = dockerClient.buildImageCmd(File(directory, "Dockerfile").apply {
                log("Building bootable server image")
                this.writeText("""
                    FROM ${template.dockerImage}
                    WORKDIR ${directory.absolutePath}
                    COPY . .
                    RUN chmod +x start.sh
                    CMD ./start.sh
                    EXPOSE ${allocation.port}
                """.trimIndent()
                )
            }).start()
            log("Fetching dynamic image id.")

            val imageId = image.awaitImageId()
            log("Loaded image id $imageId")

            var templateContainer: CreateContainerResponse? = null

            try {
                templateContainer = dockerClient.createContainerCmd(imageId)
                    .withName(if (template.persisted) template.templateKey else template.idScheme.replace("%containerId%", "${allocation.port}"))
                    .withIpv4Address(allocation.bindAddress)
                    .withExposedPorts(
                        ExposedPort.tcp(
                            allocation.port
                        )
                    )
                    .withVolumes(Volume(
                        directory.absolutePath
                    ))
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

            log("Starting container ${templateContainer.id}")
            dockerClient.startContainerCmd(templateContainer.id).exec()

            containers[templateContainer.id] = WrappedDockerContainer(
                template,
                templateContainer,
                allocation
            )

            log("Inspecting container ${templateContainer.id}")
            return dockerClient.inspectContainerCmd(templateContainer.id).exec()
        } else {
            // Handle the case where the directory exists already
        }

        return null
    }
}
