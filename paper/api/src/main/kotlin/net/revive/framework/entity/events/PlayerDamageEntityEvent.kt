package net.revive.framework.entity.events

import net.revive.framework.entity.AbstractNMSEntity
import net.revive.framework.event.SimpleEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PlayerDamageEntityEvent(
    val player: Player,
    val entity: AbstractNMSEntity
) : SimpleEvent(), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}