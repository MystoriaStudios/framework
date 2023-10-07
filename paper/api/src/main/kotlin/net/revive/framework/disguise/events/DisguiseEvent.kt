package net.revive.framework.disguise.events

import net.revive.framework.event.SimpleEvent
import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player

class DisguiseEvent(val player: Player, val disguiseInfo: DisguiseInfo) : SimpleEvent()