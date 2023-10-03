package net.mystoria.framework.deathmessage.damage.event

import net.mystoria.framework.deathmessage.damage.AbstractDamage
import net.mystoria.framework.event.SimpleEvent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class CustomPlayerDamageEvent(
    val player: Player,
    val cause: EntityDamageEvent
) : SimpleEvent() {
    var trackerDamage: AbstractDamage? = null
    val damage: Double get() = cause.getDamage(EntityDamageEvent.DamageModifier.BASE)
}
