package net.revive.framework.deployment.docker

import com.github.dockerjava.api.command.CreateContainerResponse
import net.revive.framework.allocation.Allocation
import net.revive.framework.deployment.template.DeploymentTemplate
import net.revive.framework.node.WrappedContainerHeartbeat

class WrappedDockerContainer(
    val template: DeploymentTemplate,
    val container: CreateContainerResponse,
    val allocation: Allocation,
    var heartbeat: WrappedContainerHeartbeat? = null
) {
    //todo @98Ping makee esomeething heere wheere you can access the logs of the server and make sure it is addeed to the @DeploymentRouter
    fun send(command: String) = DockerContainerController.
    fun stop() = DockerContainerController.stopContainer(container.id)
    fun kill() = DockerContainerController.killContainer(container.id)
}