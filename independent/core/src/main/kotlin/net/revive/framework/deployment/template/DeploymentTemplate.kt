package net.revive.framework.deployment.template

import net.revive.framework.FrameworkApp
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.save
import net.revive.framework.metadata.IMetaDataHolder
import java.io.File

/**
 * Class for defining deployment templates.
 *
 * @param templateKey The unique key for the template.
 * @param nameScheme The naming scheme for containers.
 * @param idScheme The ID scheme for containers.
 * @param serverExecutableOrigin The URL to download the server executable.
 * @param startupCommand The command to start the server.
 * @param dockerImage The Docker image for Java.
 * @param persisted Whether the template is persisted.
 */
data class DeploymentTemplate(
    val templateKey: String = "example",
    val nameScheme: String = "example-%containerId%",
    val idScheme: String = "example-%containerId%",
    var serverExecutableOrigin: String = "https://api.papermc.io/v2/projects/paper/versions/1.20.2/builds/278/downloads/paper-1.20.2-278.jar",
    var startupCommand: String = """
        java -Xms4096M -Xmx4096M --add-modules=jdk.incubator.vector
        -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200
        -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch
        -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4
        -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90
        -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32
        -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs
        -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40
        -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -Dterminal.ansi=true -jar %originJar%
    """.trimIndent(),
    var dockerImage: String = "bellsoft/liberica-openjdk-alpine:11.0.18",
    val persisted: Boolean = !nameScheme.contains("%containerId%"),
    override val metaData: MutableMap<String, String> = mutableMapOf()
) : IMetaDataHolder {
    @Transient val directory = File(FrameworkApp.getBaseFolder(), "templates/$templateKey")

    fun save() {
        FrameworkApp.useWithReturn { app ->
            if (!directory.exists()) directory.mkdirs()
            app.save(JsonConfig("templates/$templateKey/template.json"), this)
        }
    }
}
