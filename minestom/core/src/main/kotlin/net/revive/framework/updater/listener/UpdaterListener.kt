package net.revive.framework.updater.listener

import net.kyori.adventure.text.Component
import net.minestom.server.event.player.PlayerLoginEvent
import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.service.Configure
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.connection.JFrogUpdaterConnector
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.listener
import java.util.concurrent.TimeUnit

object UpdaterListener {

    @Configure
    fun configure() {
        listener<PlayerLoginEvent> {
            filter { event ->
                event.player.hasPermission("framework.updater.login.notifications")
            }

            execute { event ->
                val start = System.currentTimeMillis()
                val pendingUpdates = mutableListOf<Pair<String, String>>()

                JFrogUpdaterConnector
                    .usePendingUpdates { asset, _, name ->
                        pendingUpdates += name to asset.version
                    }
                    .thenRun {
                        if (pendingUpdates.isNotEmpty()) {
                            event.player.sendMessage(
                                Component.text(
                                    UpdaterService.createLoginMessage(*pendingUpdates.toTypedArray())
                                )
                            )
                        }

                        if (
                            System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L) &&
                            pendingUpdates.isEmpty()
                        ) {
                            event.player.sendMessage(
                                buildComponent(
                                    "This process took more than 5 seconds, is the artifactory alright?",
                                    Tailwind.RED_400
                                )
                            )
                        }
                    }
            }
        }
    }
}