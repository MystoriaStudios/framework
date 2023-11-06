package net.revive.framework.deployment.docker

import com.github.dockerjava.api.command.CreateContainerResponse
import net.revive.framework.allocation.Allocation
import net.revive.framework.deployment.template.DeploymentTemplate

class WrappedDockerContainer(
    val template: DeploymentTemplate,
    val container: CreateContainerResponse,
    val allocation: Allocation
)