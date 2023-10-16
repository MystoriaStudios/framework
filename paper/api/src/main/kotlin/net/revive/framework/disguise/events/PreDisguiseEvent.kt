package net.revive.framework.disguise.events

import net.revive.framework.event.SimpleEvent
import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

/**
 * Event triggered before a player is disguised, allowing for cancellation.
 *
 * @property player The player who is about to be disguised.
 * @property disguiseInfo Information about the intended disguise.
 */
class PreDisguiseEvent(val player: Player, val disguiseInfo: DisguiseInfo) : SimpleEvent(), Cancellable {

    private var bool: Boolean = false

    /** Returns whether the event has been cancelled. */
    override fun isCancelled() = bool

    /**
     * Sets the cancellation status of the event.
     *
     * @param cancel True to cancel the event, false otherwise.
     */
    override fun setCancelled(cancel: Boolean) {
        bool = cancel
    }
}
