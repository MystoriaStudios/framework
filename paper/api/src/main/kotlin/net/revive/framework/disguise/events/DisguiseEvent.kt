package net.revive.framework.disguise.events

import net.revive.framework.event.SimpleEvent
import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player

/**
 * Event triggered when a player is disguised.
 *
 * @property player The player being disguised.
 * @property disguiseInfo Information about the disguise being applied.
 */
class DisguiseEvent(val player: Player, val disguiseInfo: DisguiseInfo) : SimpleEvent()
