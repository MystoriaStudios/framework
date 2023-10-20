package net.revive.framework

import com.google.common.base.Stopwatch
import net.hollowcube.minestom.extensions.ExtensionBootstrap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.revive.framework.adapters.ComponentAdapter
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.cache.UUIDCache
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.server.ExtendedMinestomServer
import net.revive.framework.updater.UpdaterMinestomPlatform
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.UpdaterConnector
import java.util.concurrent.TimeUnit

fun main() {
    MinestomFrameworkServer.also {
        it.load()
        it.enable()
    }

    ExtensionBootstrap.init()

    Runtime.getRuntime().addShutdownHook(Thread {
        MinestomFrameworkServer.disable()
    })
}

object MinestomFrameworkServer : ExtendedMinestomServer() {
    lateinit var config: Any

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

        }

        UpdaterService.configure(UpdaterMinestomPlatform)
    }

    @ContainerDisable
    fun containerDisable() {
        UpdaterService.reload()
        UpdaterConnector.applyPendingUpdates()
        FrameworkObjectControllerCache.closeAll()
    }
}