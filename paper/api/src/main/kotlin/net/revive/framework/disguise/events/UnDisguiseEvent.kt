package net.revive.framework.disguise.events

import net.revive.framework.event.SimpleEvent
import org.bukkit.entity.Player

/**
 * Event triggered when a player's disguise is removed.
 *
 * @property player The player whose disguise is being removed.
 */
class UnDisguiseEvent(val player: Player) : SimpleEvent()
