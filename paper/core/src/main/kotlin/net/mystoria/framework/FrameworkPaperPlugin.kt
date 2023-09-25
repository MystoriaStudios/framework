package net.mystoria.framework

import me.lucko.helper.Events
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import net.mystoria.framework.annotation.container.ContainerDisable
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.annotation.container.ContainerPreEnable
import net.mystoria.framework.menu.FrameworkMenuHandler
import net.mystoria.framework.menu.IMenuHandler
import net.mystoria.framework.nms.INMSVersion
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import net.mystoria.framework.plugin.event.KotlinPluginEnableEvent
import net.mystoria.framework.updater.UpdaterPaperPlatform
import net.mystoria.framework.updater.UpdaterService
import net.mystoria.framework.updater.connection.UpdaterConnector
import org.bukkit.Bukkit

@Plugin(
    name = "Framework",
    version = "1.0.11-SNAPSHOT",
    authors = ["Mystoria Studios"],
    website = "https://mystoria.net/",
)
@HelperImplementationPlugin
class FrameworkPaperPlugin : ExtendedKotlinPlugin() {

    companion object {
        lateinit var instance: FrameworkPaperPlugin
    }

    lateinit var nmsVersion: INMSVersion

    @ContainerPreEnable
    fun containerPreEnable() {
        Bukkit.getCommandMap().knownCommands.remove("plugins")
    }

    @ContainerEnable
    fun containerEnable() {
        instance = this
        Framework.supply(PaperFramework) {
            it.flavor = flavor()
            it.flavor.bind<IMenuHandler>() to FrameworkMenuHandler()
        }

        nmsVersion = getNMSInstance()

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,

        // uses the event from the plugin so we can just do extra logic as required :D
        Events.subscribe(KotlinPluginEnableEvent::class.java).handler { event ->
            PaperFramework.registerInternalPlugin(event.plugin)
        }
    }

    @ContainerDisable
    fun containerDisable() {
        UpdaterService.reload()
        UpdaterConnector.applyPendingUpdates()
    }

    private fun getNMSInstance(): INMSVersion {
        var packageName = server.javaClass.getPackage().name;
        packageName = packageName.substring(packageName.lastIndexOf('.') + 1);

        Framework.use {
            it.log("Framework", "Loaded NMS Version: net.mystoria.framework.nms.${packageName.toUpperCase()}Version")
        }

        val clazz = Class.forName("net.mystoria.framework.nms.${packageName.toUpperCase()}Version")
        return clazz.getDeclaredConstructor().newInstance() as INMSVersion
    }
}