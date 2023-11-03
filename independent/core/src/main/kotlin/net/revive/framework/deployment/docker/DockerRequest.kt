package net.revive.framework.deployment.docker

import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.DeploymentService

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
abstract class DockerRequest(
    val client: DockerHttpClient = DeploymentService.dockerClient
) {
    abstract fun call() : DockerHttpClient.Response
}