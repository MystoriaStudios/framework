package net.revive.framework

import me.lucko.helper.Events
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.config.load
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.menu.FrameworkMenuHandler
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import net.revive.framework.plugin.ExtendedKotlinPlugin
import net.revive.framework.plugin.event.KotlinPluginEnableEvent
import net.revive.framework.updater.UpdaterPaperPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.UpdaterConnector
import net.revive.framework.utils.Tasks
import org.bukkit.Bukkit
import kotlin.reflect.KClass

@Plugin(
    name = "Framework",
    version = "1.0.11-SNAPSHOT",
    authors = ["Revive Studios"],
    website = "https://randomcraft.net/",
    depends = [
        PluginDependency("spark")
    ]
)
@HelperImplementationPlugin
class PaperFrameworkPlugin : ExtendedKotlinPlugin() {

    companion object {
        lateinit var instance: PaperFrameworkPlugin
    }

    lateinit var nmsVersion: NMSVersion

    @ContainerPreEnable
    fun containerPreEnable() {
        net.revive.framework.Framework.supply(PaperFramework) {
            it.flavor = flavor()
        }

        Bukkit.getCommandMap().knownCommands.remove("plugins")
    }

    @ContainerEnable
    fun containerEnable() {
        instance = this
        net.revive.framework.Framework.use { framework ->
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler()
        }

        nmsVersion = getNMSVersion()

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the implementation here O,

        Tasks.plugin = this

        // uses the event from the plugin so we can just do extra logic as required :D
        Events.subscribe(KotlinPluginEnableEvent::class.java).handler { event ->
            PaperFramework.registerInternalPlugin(event.plugin)
        }

        this.packageIndexer
            .getTypesAnnotatedWith<NMSHandler>()
            .mapNotNull {
                it.kotlin.objectInstance
            }
            .filter {
                it.javaClass.getAnnotation(NMSHandler::class.java).version == nmsVersion
            }
            .forEach { instance ->
                instance.javaClass.interfaces.forEach { interfaceClass ->
                    net.revive.framework.Framework.use {
                        it.flavor.bindRaw(interfaceClass.kotlin) to instance
                    }
                }
            }

    }

    @ContainerDisable
    fun containerDisable() {
        UpdaterService.reload()
        UpdaterConnector.applyPendingUpdates()
    }

    private fun getNMSVersion(): NMSVersion
    {
        var packageName = server.javaClass.getPackage().name;
        packageName = packageName.substring(packageName.lastIndexOf('.') + 1);

        return NMSVersion.valueOf(packageName.toUpperCase())
    }

    private fun Flavor.bindRaw(klass: KClass<*>): FlavorBinder<Any>
    {
        val binder = FlavorBinder(klass)
        binders.add(binder)
        return binder
    }

}