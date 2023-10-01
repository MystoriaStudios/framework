package net.mystoria.framework

import me.lucko.helper.Events
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import net.mystoria.framework.annotation.container.ContainerDisable
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.annotation.container.ContainerPreEnable
import net.mystoria.framework.config.load
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorBinder
import net.mystoria.framework.menu.FrameworkMenuHandler
import net.mystoria.framework.menu.IMenuHandler
import net.mystoria.framework.nms.NMSVersion
import net.mystoria.framework.nms.annotation.NMSHandler
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import net.mystoria.framework.plugin.event.KotlinPluginEnableEvent
import net.mystoria.framework.updater.UpdaterPaperPlatform
import net.mystoria.framework.updater.UpdaterService
import net.mystoria.framework.updater.connection.UpdaterConnector
import net.mystoria.framework.utils.Tasks
import org.bukkit.Bukkit
import kotlin.reflect.KClass

@Plugin(
    name = "Framework",
    version = "1.0.11-SNAPSHOT",
    authors = ["Mystoria Studios"],
    website = "https://mystoria.net/",
)
@HelperImplementationPlugin
class PaperFrameworkPlugin : ExtendedKotlinPlugin() {

    companion object {
        lateinit var instance: PaperFrameworkPlugin
    }

    lateinit var nmsVersion: NMSVersion

    @ContainerPreEnable
    fun containerPreEnable() {
        Framework.supply(PaperFramework) {
            it.flavor = flavor()
        }

        Bukkit.getCommandMap().knownCommands.remove("plugins")
    }

    @ContainerEnable
    fun containerEnable() {
        instance = this
        Framework.use { framework ->
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler()
        }

        nmsVersion = getNMSVersion()

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,

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
                    Framework.use {
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