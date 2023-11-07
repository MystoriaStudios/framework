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
)