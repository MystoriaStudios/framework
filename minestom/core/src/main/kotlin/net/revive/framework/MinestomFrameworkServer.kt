package net.revive.framework

import com.google.common.primitives.Ints
import fr.bretzel.minestom.placement.BlockPlacementManager
import net.hollowcube.minestom.extensions.ExtensionBootstrap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.minestom.server.MinecraftServer
import net.revive.framework.adapters.ComponentAdapter
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.console.Console
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.grpc.FrameworkGRPCClient
import net.revive.framework.item.FrameworkItemStack
import net.revive.framework.item.IItemStackProvider
import net.revive.framework.item.MinestomFrameworkItemStack
import net.revive.framework.item.MinestomItemStackProvider
import net.revive.framework.menu.FrameworkMenuHandler
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.server.ExtendedMinestomServer
import net.revive.framework.server.IMinecraftPlatform
import net.revive.framework.updater.UpdaterMinestomPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.JFrogUpdaterConnector
import net.revive.framework.utils.Tasks
import org.fusesource.jansi.AnsiConsole
import java.io.File
import java.time.Duration
import java.util.Properties
import kotlin.concurrent.thread

fun main() {
    System.setProperty("org.fusesource.jansi.Ansi.disable", "false")
    System.setProperty("jansi.passthrough", "true")
    System.setProperty("org.jline.terminal", "org.jline.terminal.impl.JansiSupport")
    AnsiConsole.systemInstall()

    val server = ExtensionBootstrap.init()

    MinestomFrameworkServer.also {
        it.load()
        it.enable()
    }

    MinecraftServer.setTerminalEnabled(true)
    BlockPlacementManager.register();
    MinecraftServer.setBrandName("Framework Server")

    System.setProperty("minestom.chunk-view-distance", 8.toString())
    System.setProperty("minestom.entity-view-distance", 8.toString())

    File("server.properties").apply {
        if (this.exists()) {
            val properties = Properties()
            properties.load(this.bufferedReader())

            server.start(
                properties.getProperty("server-host") ?: "0.0.0.0",
                Ints.tryParse(properties.getProperty("server-port")) ?: 25565
            )
        } else {
            server.start("0.0.0.0", 25565)
        }
    }


    MinestomFrameworkServer.terminalThread = thread(start = true, isDaemon = true, name = "FrameworkConsole") {
        Console.start()
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        MinestomFrameworkServer.disable()
    })
}

object MinestomFrameworkServer : ExtendedMinestomServer() {

    lateinit var terminalThread: Thread

    @ContainerPreEnable
    fun containerPreEnable() {
        Framework.supply(MinestomFramework) {
            it.flavor = flavor()

            if (it.serializer is GsonSerializer) {
                (it.serializer as GsonSerializer).useGsonBuilderThenRebuild { gson ->
                    gson.registerTypeAdapter(Component::class.java, ComponentAdapter)
                    gson.registerTypeAdapter(TextComponent::class.java, ComponentAdapter)
                }
            }
        }
    }

    @ContainerEnable
    fun containerEnable() {
        Framework.use { framework ->
            framework.flavor.bind<IMenuHandler>() to FrameworkMenuHandler
            framework.flavor.bind<IMinecraftPlatform>() to MinestomMinecraftPlatform

            // pleasse do not inject 3 different instances of the fucking item stack provideer again nopox..
            val itemStackProvider = MinestomItemStackProvider()
            framework.flavor.bind<IItemStackProvider<*>>() to itemStackProvider
            framework.flavor.bind<IItemStackProvider<FrameworkItemStack>>() to itemStackProvider
            framework.flavor.bind<IItemStackProvider<MinestomFrameworkItemStack>>() to itemStackProvider
            framework.flavor.inject(FrameworkGRPCClient)
        }


        FrameworkGRPCClient.configure()

        // state tasks for minestom
        Tasks.asyncTimer(Duration.ofSeconds(1L)) {
            MinestomMinecraftPlatform.getOnlinePlayers().forEach {
                it.states.forEach { state ->
                    state.tick(it)
                }
            }
        }

        UpdaterService.configure(UpdaterMinestomPlatform)
    }

    @ContainerDisable
    fun containerDisable() {
        UpdaterService.reload()
        JFrogUpdaterConnector.applyPendingUpdates()
        FrameworkObjectControllerCache.closeAll()
    }
}