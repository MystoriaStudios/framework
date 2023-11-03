package net.revive.framework.deployment.template

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
data class DeploymentTemplate(
    val nameScheme: String = "example-%containerid%",
    val idScheme: String = "EXP-%containerId%"
)