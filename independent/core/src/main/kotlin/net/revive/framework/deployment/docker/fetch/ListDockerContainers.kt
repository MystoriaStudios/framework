package net.revive.framework.deployment.docker.fetch

import com.github.dockerjava.api.model.Container
import net.revive.framework.deployment.DeploymentService

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
object ListDockerContainers {
   fun call(): List<Container> = DeploymentService.dockerClient.listContainersCmd().exec()
}