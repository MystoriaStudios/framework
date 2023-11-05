package net.revive.framework

import co.aikar.commands.CommandManager
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
import net.revive.framework.command.FrameworkCommandManager
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.disguise.FrameworkDisguiseHandler
import net.revive.framework.disguise.IDisguiseHandler
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.grpc.FrameworkGRPCClient
import net.revive.framework.item.FrameworkItemStack
import net.revive.framework.item.IItemStackProvider
import net.revive.framework.item.PaperFrameworkItemStack
import net.revive.framework.item.PaperItemStackProvider
import net.revive.framework.maven.MavenLibrary
import net.revive.framework.maven.Repository
import net.revive.framework.menu.FrameworkMenuHandler
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import net.revive.framework.nms.menu.INMSMenuHandler
import net.revive.framework.nms.menu.V1_20_R1MenuHandler
import net.revive.framework.permission.impl.LuckPermsPermissionProvider
import net.revive.framework.plugin.ExtendedKotlinPlugin
import net.revive.framework.plugin.event.KotlinPluginEnableEvent
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.server.IMinecraftPlatform
import net.revive.framework.updater.UpdaterPaperPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.JFrogUpdaterConnector
import net.revive.framework.utils.GsonFactory
import net.revive.framework.utils.Tasks
import net.revive.framework.visibility.FrameworkVisiblityHandler
import net.revive.framework.visibility.IVisibilityHandler
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

@Plugin(
    name = "Framework",
    version = "1.0.14-SNAPSHOT",
    authors = ["Revive Studios"],
    apiVersion = "1.20",
    website = "https://randomcraft.net/",
    depends = [
        PluginDependency(
            value = "LuckPerms",
            soft = true
        )
    ]
)
@HelperImplementationPlugin
@MavenLibrary(
    "net.kyori",
    "adventure-text-serializer-gson",
    "4.10.1",
)
@MavenLibrary(
    "net.kyori",
    "adventure-text-minimessage",
    "4.10.1"
)
@MavenLibrary(
    "fr.mrmicky",
    "fastboard",
    "2.0.0"
)
@MavenLibrary(
    "com.github.cryptomorin",
    "XSeries",
    "9.5.0"
)
@MavenLibrary(
    "com.squareup.retrofit2",
    "retrofit",
    "2.9.0",

)
@MavenLibrary(
    "com.squareup.retrofit2",
    "converter-gson",
    "2.9.0"
)
@MavenLibrary(
    "io.grpc",
    "grpc-netty",
    "1.57.2"
)
@MavenLibrary(
    "io.grpc",
    "grpc-protobuf",
    "1.57.2"
)
@MavenLibrary(
    "com.google.protobuf",
    "protobuf-java-util",
    "3.24.1"
)
@MavenLibrary(
    "io.grpc",
    "grpc-kotlin-stub",
    "1.4.0"
)
@MavenLibrary(
    "org.reflections",
    "reflections",
    "0.10.2"
)
@MavenLibrary(
    "com.github.ben-manes.caffeine",
    "caffeine",
    "3.1.8"
)
@MavenLibrary(
    "org.mongodb",
    "mongo-java-driver",
    "3.12.11"
)
@MavenLibrary(
    "io.lettuce",
    "lettuce-core",
    "6.2.4-RELEASE"
)
@MavenLibrary(
    "com.google.code.gson",
    "gson",
    "2.9.0"
)
@MavenLibrary(
    "io.sentry",
    "sentry",
    "6.29.0"
)
@MavenLibrary(
    "com.github.docker-java",
    "docker-java",
    "3.3.4"
)
@MavenLibrary(
    "com.github.robinbraemer",
    "CloudflareAPI",
    "1.4.1"
)
@MavenLibrary(
    "com.google.guava",
    "guava",
    "31.0.1-jre"
)
@MavenLibrary(
    "commons-io",
    "commons-io",
    "2.11.0"
)
class PaperFrameworkPlugin : ExtendedKotlinPlugin() {

    companion object {
        lateinit var instance: PaperFrameworkPlugin
    }

    lateinit var nmsVersion: NMSVersion

    @ContainerPreEnable
    fun containerPreEnable() {
        Tasks.plugin = this
        saveDefaultConfig()
        Framework.supply(PaperFramework) {
            it.flavor = flavor()

            if (it.serializer is GsonSerializer) {
                GsonFactory.applyPlatformChanges()
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
            framework.configure(PaperFrameworkPlatform)
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler
            framework.flavor.bind<IMinecraftPlatform>() to PaperMinecraftPlatform
            framework.flavor.bind<IFrameworkPlatform>() to PaperFrameworkPlatform


            val itemStackProvider = PaperItemStackProvider()
            framework.flavor.bind<IItemStackProvider<*>>() to itemStackProvider
            framework.flavor.bind<IItemStackProvider<FrameworkItemStack>>() to itemStackProvider
            framework.flavor.bind<IItemStackProvider<PaperFrameworkItemStack>>() to itemStackProvider

            framework.flavor.bind<IDisguiseHandler>() to FrameworkDisguiseHandler()
            framework.flavor.bind<IVisibilityHandler>() to FrameworkVisiblityHandler()

            framework.flavor.bind<INMSMenuHandler>() to V1_20_R1MenuHandler

            framework.flavor.inject(PaperMinecraftPlatform)
            framework.flavor.inject(FrameworkGRPCClient)
        }

        FrameworkGRPCClient.configure()

        Tasks.asyncTimer(2L, 2L) {
            PaperMinecraftPlatform.getOnlinePlayers().forEach {
                it.states.forEach { state ->
                    state.tick(it)
                }
            }
        }

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,

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
        JFrogUpdaterConnector.applyPendingUpdates()
        FrameworkObjectControllerCache.closeAll()
    }

    private fun getNMSVersion(): NMSVersion {
        var packageName = server.javaClass.getPackage().name;
        packageName = packageName.substring(packageName.lastIndexOf('.') + 1);

        return NMSVersion.valueOf(packageName.uppercase())
    }
}