package net.revive.framework.deployment.template

import net.revive.framework.FrameworkApp
import net.revive.framework.FrameworkNodePlatform
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save

/**
 * Class created on 11/2/2023

 * @author 98ping
 * @project framework
 * @website https://solo.to/redis
 */
data class DeploymentTemplate(
    val templateKey: String = "example",
    val nameScheme: String = "example-%containerId%",
    val idScheme: String = "EXP-%containerId%",
    val startupCommand: String = "java -jar server.jar",
    val dockerImage: String = "ghcr.io/pterodactyl/yolks:java_18",
    val persisted: Boolean = nameScheme.contains("%containerId%"),
) {
    fun save() {
        FrameworkApp.useWithReturn { app ->
            app.save(JsonConfig("persistent/$nameScheme.json"), this)
        }
    }
}