package net.revive.framework

import com.google.common.base.Stopwatch
import me.lucko.helper.Events
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.actionlog.Action
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.menu.FrameworkMenuHandler
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import net.revive.framework.permission.impl.LuckPermsPermissionProvider
import net.revive.framework.plugin.ExtendedKotlinPlugin
import net.revive.framework.plugin.event.KotlinPluginEnableEvent
import net.revive.framework.updater.UpdaterPaperPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.UpdaterConnector
import net.revive.framework.utils.Tasks
import net.revive.framework.visibility.FrameworkVisiblityHandler
import net.revive.framework.visibility.IVisibilityHandler
import org.bukkit.Bukkit
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Plugin(
    name = "Framework",
    version = "1.0.13-SNAPSHOT",
    authors = ["Revive Studios"],
    website = "https://randomcraft.net/",
    depends = [
        PluginDependency(
            value = "LuckPerms",
            soft = true
        )
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
        saveDefaultConfig()
        Framework.supply(PaperFramework) {
            it.flavor = flavor()
        }
    }

    @ContainerEnable
    fun containerEnable() {
        instance = this
        Framework.use { framework ->
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler()
            framework.flavor.bind<IVisibilityHandler>() to FrameworkVisiblityHandler()
        }

        nmsVersion = getNMSVersion()

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,

        Tasks.plugin = this

        // uses the event from the plugin so we can just do extra logic as required :D
        Events.subscribe(KotlinPluginEnableEvent::class.java).handler { event ->
            PaperFramework.registerInternalPlugin(event.plugin)
        }

        // brr brrr luckperms impl;ementation stupidd shit here please TYYY
        runCatching {
            Framework.use {
                val start = Stopwatch.createStarted()
                val luckPerms = LuckPermsProvider.get()
                it.log("Framework", "Hello there luckperms, i'm just gonna yoink some of your data alrighti?")
                it.permissionProvider = LuckPermsPermissionProvider(luckPerms)
                it.log("Framework", "tyvm, i yoinked and setup your data in aboutt, uhhhh ${start.elapsed(TimeUnit.MILLISECONDS)}")
            }
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