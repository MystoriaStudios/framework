package net.revive.framework.deployment.docker

import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.command.InspectImageResponse
import com.github.dockerjava.api.model.Container
import net.revive.framework.deployment.DeploymentService

/**
 * Class created on 11/3/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
object DockerContainerController {

    fun listContainers(): List<Container> = DeploymentService.dockerClient.listContainersCmd().exec()

    fun getContainerInspection(containerId: String): InspectContainerResponse =
        DeploymentService.dockerClient.inspectContainerCmd(containerId).exec()

    fun killContainer(containerId: String) {
        DeploymentService.dockerClient.killContainerCmd(containerId).exec()
    }

    fun removeContainer(containerId: String) {
        DeploymentService.dockerClient.removeContainerCmd(containerId).exec()
    }
}