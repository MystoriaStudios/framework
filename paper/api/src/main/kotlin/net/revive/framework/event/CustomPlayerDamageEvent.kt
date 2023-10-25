package net.revive.framework.event

import net.revive.framework.deathmessage.damage.AbstractDamage
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent

class CustomPlayerDamageEvent(
    val player: Player,
    val cause: EntityDamageEvent
) : SimpleEvent() {
    var trackerDamage: AbstractDamage? = null
    val damage: Double get() = cause.finalDamage // should fix that dep coz DamageModifiers are deprecated since 2016 lol.
}
