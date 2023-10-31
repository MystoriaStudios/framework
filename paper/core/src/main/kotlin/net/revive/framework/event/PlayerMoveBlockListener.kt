package net.revive.framework.event

import net.revive.framework.annotation.Listeners
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Listeners
object PlayerMoveBlockListener : Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.from.blockX != event.to.blockX || event.from.blockY != event.to.blockY || event.from.blockZ != event.to.blockZ) {
            event.isCancelled = !PlayerMoveBlockEvent(
                event.player,
                event.from.toBlockLocation(),
                event.to.toBlockLocation()
            ).callEvent()
        }
    }
}