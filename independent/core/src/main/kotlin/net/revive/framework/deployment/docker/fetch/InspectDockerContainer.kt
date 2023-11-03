package net.revive.framework.deployment.docker.fetch

import com.github.dockerjava.api.command.InspectImageResponse
import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.DeploymentService

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
class InspectDockerContainer(
    private val containerId: String
) {
    fun call(): InspectImageResponse = DeploymentService.dockerClient.inspectImageCmd(containerId).exec()
}