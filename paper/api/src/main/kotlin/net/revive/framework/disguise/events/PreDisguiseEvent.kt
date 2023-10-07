package net.revive.framework.disguise.events

import net.revive.framework.event.SimpleEvent
import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable

class PreDisguiseEvent(val player: Player, val disguiseInfo: DisguiseInfo) : SimpleEvent(), Cancellable {

    private var bool: Boolean = false

    override fun isCancelled() = bool

    override fun setCancelled(cancel: Boolean) {
        bool = cancel
    }
}