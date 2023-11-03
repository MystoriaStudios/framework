package net.revive.framework.deployment

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.FrameworkApp
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.deployment.docker.container.ListDockerContainers
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
        .build();

    val dockerClient: OkDockerHttpClient = OkDockerHttpClient.Builder()
        .dockerHost(dockerConfig.dockerHost)
        .sslConfig(dockerConfig.sslConfig)
        .connectTimeout(30 * 60)
        .readTimeout(45 * 60)
        .build()

    private val templates: MutableMap<String, DeploymentTemplate> = mutableMapOf()

    @Configure
    fun configure() {
        FrameworkApp.use { app ->
            val directory = File(app.getBaseFolder(), "persistent")

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
    }

    fun deploy(template: DeploymentTemplate) {} // TO RETURN THE CONTAINER IT GETS DEEPLOYEEDE TO
}