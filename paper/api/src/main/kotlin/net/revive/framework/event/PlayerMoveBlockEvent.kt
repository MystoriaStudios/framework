package net.revive.framework.event

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PlayerMoveBlockEvent(val player: Player, val from: Location, val to: Location) : SimpleEvent(), Cancellable {
    var cancelledBool = false

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    override fun isCancelled() = cancelledBool

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    override fun setCancelled(cancel: Boolean) {
        cancelledBool = cancel
    }
}