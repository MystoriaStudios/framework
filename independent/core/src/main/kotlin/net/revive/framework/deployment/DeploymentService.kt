package net.revive.framework.deployment

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.DockerClientConfig
import net.revive.framework.FrameworkApp
import net.revive.framework.allocation.AllocationService
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.deployment.template.DeploymentTemplate
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import java.io.File

@Service
object DeploymentService {

    // Eager docker configuration on a locally hosted instance
    private val dockerConfig: DockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("tcp://localhost:2376")
        .build()

    val dockerClient: DockerClient = DockerClientBuilder.getInstance(dockerConfig).build()

    val templates: MutableMap<String, DeploymentTemplate> = mutableMapOf()

    @Configure
    fun configure() {
        FrameworkApp.use { app ->
            val tempDirectory = File(app.getBaseFolder(), ".containers")

            if (!tempDirectory.exists()) {
                tempDirectory.mkdir()
            }

            val peristDirectory = File(app.getBaseFolder(), "persistent")

            if (!peristDirectory.exists()) {
                peristDirectory.mkdir()
            }

            val directory = File(app.getBaseFolder(), "templates")

            if (!directory.exists()) {
                directory.mkdir()
            }

            directory.listFiles().forEach { file ->
                app.load(
                    JsonConfig(file.name),
                    DeploymentTemplate::class
                ).apply {
                    val template = this as DeploymentTemplate
                    templates[template.templateKey] = template
                }
            }
        }

        if (templates.isEmpty()) {
            templates["example"] = DeploymentTemplate()
        }
    }

    @Close
    fun close() {
        templates.values.forEach(DeploymentTemplate::save)
        FrameworkApp.use { app ->
            File(app.getBaseFolder(), ".containers").delete()
        }
    }

    fun deploy(template: DeploymentTemplate): InspectContainerResponse {
        val allocation = AllocationService.take()

        // Create container based on name, port, and image
        val templateContainer: CreateContainerResponse =
            dockerClient.createContainerCmd(template.dockerImage)
                .withName(template.nameScheme)
                .withIpv4Address(allocation.bindAddress)
                .withExposedPorts(
                    ExposedPort.tcp(
                        allocation.port
                    )
                )
                .withWorkingDir(FrameworkApp.useWithReturn {
                    File(it.getBaseFolder(), if (template.persisted) "persistent" else ".containers").absolutePath
                })
                .withCmd(template.startupCommand)
                .withAttachStderr(false)
                .withAttachStdin(false)
                .withAttachStdout(false)
                .exec()

        // Start container
        dockerClient.startContainerCmd(templateContainer.id).exec();

        // Return inspection
        return dockerClient.inspectContainerCmd(templateContainer.id).exec()
    }
}