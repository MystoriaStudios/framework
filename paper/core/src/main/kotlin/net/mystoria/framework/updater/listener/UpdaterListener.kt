package net.mystoria.framework.updater.listener

import net.mystoria.framework.annotation.Listeners
import net.mystoria.framework.updater.UpdaterService
import net.mystoria.framework.updater.connection.UpdaterConnector
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.concurrent.TimeUnit

@Listeners
object UpdaterListener : Listener {

    var filterPlayerCanReceiveNotifications = { _: Player -> true }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent)
    {
        if (
            event.player.hasPermission("framework.updater.login.notifications") &&
            filterPlayerCanReceiveNotifications.invoke(event.player)
        )
        {
            val start = System.currentTimeMillis()
            val pendingUpdates = mutableListOf<Pair<String, String>>()

            UpdaterConnector
                .usePendingUpdates { asset, _, name ->
                    pendingUpdates += name to asset.version
                }
                .thenRun {
                    if (pendingUpdates.isNotEmpty())
                    {
                        event.player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            UpdaterService.createLoginMessage(*pendingUpdates.toTypedArray())
                        ))
                    }

                    if (
                        System.currentTimeMillis() - start >= TimeUnit.SECONDS.toMillis(5L) &&
                        pendingUpdates.isEmpty()
                    )
                    {
                        event.player.sendMessage(
                            "${ChatColor.RED}This process took more than 5 seconds, is the artifactory alright?"
                        )
                    }
                }
        }
    }
}
