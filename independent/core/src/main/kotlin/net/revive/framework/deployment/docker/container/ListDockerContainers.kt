package net.revive.framework.deployment.docker.container

import com.github.dockerjava.okhttp.OkDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.docker.DockerRequest
import java.io.UncheckedIOException

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
object ListDockerContainers : DockerRequest() {
    override fun call(): DockerHttpClient.Response {
        val toMake = DockerHttpClient.Request.builder()
            .method(DockerHttpClient.Request.Method.GET)
            .path("/containers/json")
            .headers(
                mutableMapOf(
                    "Content-Type" to "application/json"
                )
            )
            .build()

        // If it bottoms out, docker-java throws an error
        // for us.
        return client.execute(toMake)
    }
}