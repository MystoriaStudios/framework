package net.revive.framework.deployment.template

import net.revive.framework.FrameworkApp
import net.revive.framework.FrameworkNodePlatform
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import java.io.File

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
    val serverExecutableOrigin: String = "https://api.papermc.io/v2/projects/paper/versions/1.20.2/builds/278/downloads/paper-1.20.2-278.jar", // SASME SHIT DIFFERENT DAY JUST DOWNLOADS THE JAR FROM THE LINK THEREE MHM AND ALLOWS YOU TO SEE UPDATESS IN THE PANEEL WHOOOOO YES OKAY BYE
    val startupCommand: String = "java -Xms4096M -Xmx4096M --add-modules=jdk.incubator.vector -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -Dterminal.ansi=true -jar %originJar%", // THISS WILL BEE T HE STARTUP COMMAND MAKEE SUREE TO INCLUDEE %originJar% OR U WILL F UCK UP THEE DOWNLOADDEEEEEERRRR
    val dockerImage: String = "bellsoft/liberica-openjdk-alpine:11.0.18", // THIS WILL BEE THE JAVA VERSION!
    val persisted: Boolean = !nameScheme.contains("%containerId%"),
) {
    @Transient val directory = File(FrameworkApp.getBaseFolder(), "templates/$templateKey")

    fun save() {
        FrameworkApp.useWithReturn { app ->
            if (!directory.exists()) directory.mkdirs()

            app.save(JsonConfig("templates/$templateKey/template.json"), this)
        }
    }
}