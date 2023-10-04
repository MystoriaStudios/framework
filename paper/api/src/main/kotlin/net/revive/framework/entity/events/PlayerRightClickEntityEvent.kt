package net.revive.framework.entity.events

import net.revive.framework.entity.AbstractNMSEntity
import net.revive.framework.event.SimpleEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PlayerRightClickEntityEvent(
    val player: Player,
    val entity: AbstractNMSEntity
) : SimpleEvent(), Cancellable
{

    private var cancelled: Boolean = false
    var spamProtection: Boolean = true

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}