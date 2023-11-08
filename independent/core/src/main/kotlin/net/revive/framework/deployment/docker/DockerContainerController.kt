package net.revive.framework.deployment.docker

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.command.InspectContainerResponse
import com.github.dockerjava.api.model.Container
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.core.command.LogContainerResultCallback
import net.revive.framework.deployment.DeploymentService

/**
 * Class created on 11/3/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
object DockerContainerController {

    fun listContainers(): List<Container> = DeploymentService.dockerClient.listContainersCmd().exec()

    fun getContainerLogs(containerId: String) : MutableList<String> {
        val logs = mutableListOf<String>()

        val command = DeploymentService.dockerClient.logContainerCmd(containerId)
        command.withStdOut(true).withStdErr(true)
        command.withSince((System.currentTimeMillis() / 1000).toInt() + 5)

        command.withTimestamps(true)

        try {
            command.exec(object : ResultCallback.Adapter<Frame>() {
                override fun onNext(item: Frame) {
                    logs.add(item.toString())
                }
            }).awaitCompletion()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return logs
    }

    fun getContainerInspection(containerId: String): InspectContainerResponse =
        DeploymentService.dockerClient.inspectContainerCmd(containerId).exec()

    fun killContainer(containerId: String) {
        DeploymentService.dockerClient.killContainerCmd(containerId).exec()
    }

    fun removeContainer(containerId: String) {
        DeploymentService.dockerClient.removeContainerCmd(containerId).exec()
    }
}