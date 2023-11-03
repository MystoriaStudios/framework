package net.revive.framework.deployment

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.docker.container.ListDockerContainers
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service

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
        .build();

    @Configure
    fun configure() {

    }
}