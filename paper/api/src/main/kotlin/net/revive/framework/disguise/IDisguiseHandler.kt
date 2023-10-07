package net.revive.framework.disguise

import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player

interface IDisguiseHandler {
    fun isDisguised(player: Player): Boolean
    fun disguise(player: Player, disguiseInfo: DisguiseInfo, callback: () -> Unit = {})
    fun unDisguise(player: Player, disconnecting: Boolean, callback: () -> Unit)
}