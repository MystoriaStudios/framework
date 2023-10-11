package net.revive.framework

import com.google.common.base.Stopwatch
import me.lucko.helper.Events
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.luckperms.api.LuckPermsProvider
import net.revive.framework.adapters.ComponentAdapter
import net.revive.framework.adapters.ItemStackAdapter
import net.revive.framework.adapters.LocationAdapter
import net.revive.framework.adapters.WorldAdapter
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.cache.PaperLocalUUIDCacheTranslator
import net.revive.framework.cache.UUIDCache
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.menu.FrameworkMenuHandler
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import net.revive.framework.permission.impl.LuckPermsPermissionProvider
import net.revive.framework.plugin.ExtendedKotlinPlugin
import net.revive.framework.plugin.event.KotlinPluginEnableEvent
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.updater.UpdaterPaperPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.UpdaterConnector
import net.revive.framework.utils.Tasks
import net.revive.framework.visibility.FrameworkVisiblityHandler
import net.revive.framework.visibility.IVisibilityHandler
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Plugin(
    name = "Framework",
    version = "1.0.14-SNAPSHOT",
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

            if (it.serializer is GsonSerializer) {
                (it.serializer as GsonSerializer).useGsonBuilderThenRebuild { gson ->
                    gson.registerTypeAdapter(Component::class.java, ComponentAdapter)
                    gson.registerTypeAdapter(TextComponent::class.java, ComponentAdapter)
                    gson.registerTypeAdapter(ItemStack::class.java, ItemStackAdapter)
                    gson.registerTypeAdapter(Location::class.java, LocationAdapter)
                    gson.registerTypeAdapter(World::class.java, WorldAdapter)
                    it.log("Framework", "hehehhe sso so so right so, i just did the gson things hjehehe")
                }
            }
        }
    }

    @ContainerEnable
    fun containerEnable() {
        instance = this
        nmsVersion = NMSVersion.V1_20_R1

        Framework.use { framework ->
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler
            framework.flavor.bind<IVisibilityHandler>() to FrameworkVisiblityHandler()
        }

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,

        Tasks.plugin = this

        // RESOLVEE UUIDS
        UUIDCache.configure(PaperLocalUUIDCacheTranslator())

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
                it.log(
                    "Framework",
                    "tyvm, i yoinked and setup your data in aboutt, uhhhh ${start.elapsed(TimeUnit.MILLISECONDS)}"
                )
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
                        it.flavor.binders.add(FlavorBinder(interfaceClass.kotlin) to instance)
                    }
                }
            }
    }

    @ContainerDisable
    fun containerDisable() {
        UpdaterService.reload()
        UpdaterConnector.applyPendingUpdates()
    }

    private fun getNMSVersion(): NMSVersion {
        var packageName = server.javaClass.getPackage().name;
        packageName = packageName.substring(packageName.lastIndexOf('.') + 1);

        return NMSVersion.valueOf(packageName.uppercase())
    }
}