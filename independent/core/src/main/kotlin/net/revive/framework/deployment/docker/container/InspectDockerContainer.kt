package net.revive.framework.deployment.docker.container

import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.docker.DockerRequest

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
class InspectDockerContainer(
    private val containerId: String
) : DockerRequest() {

    override fun call(): DockerHttpClient.Response {
        val toMake = DockerHttpClient.Request.builder()
            .method(DockerHttpClient.Request.Method.GET)
            .path("/containers/$containerId/json")
            .headers(
                mutableMapOf(
                    "Content-Type" to "application/json"
                )
            )
            .build()

        return client.execute(toMake)
    }
}