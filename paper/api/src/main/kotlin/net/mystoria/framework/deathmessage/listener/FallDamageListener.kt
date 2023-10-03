package net.mystoria.framework.deathmessage.listener

import net.mystoria.framework.annotation.Listeners
import net.mystoria.framework.deathmessage.DeathMessageService
import net.mystoria.framework.deathmessage.damage.AbstractDamage
import net.mystoria.framework.deathmessage.damage.PlayerAbstractDamage
import net.mystoria.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object FallDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause.cause !== EntityDamageEvent.DamageCause.FALL) {
            return
        }
        val record: List<AbstractDamage> = DeathMessageService.getDamage(event.player)
        var knocker: AbstractDamage? = null
        var knockerTime = 0L
        for (damage in record) {
            if (damage !is FallDamage) {
                if (damage is FallDamageByPlayer) {
                    continue
                }
                if (damage !is PlayerAbstractDamage || knocker != null && damage.time <= knockerTime) {
                    continue
                }
                knocker = damage
                knockerTime = damage.time
            }
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.trackerDamage = FallDamageByPlayer(
                event.player.uniqueId,
                event.damage,
                (knocker as PlayerAbstractDamage).damager
            )

        } else {
            event.trackerDamage = FallDamage(event.player.uniqueId, event.damage)
        }
    }

    class FallDamage(damaged: UUID, damage: Double) : AbstractDamage(damaged, damage) {
        override fun getDeathMessage(player: UUID): String {
            return (wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " hit the ground too hard."
        }
    }

    class FallDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) : PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " hit the ground too hard thanks to " + wrapName(
                this.damager,
                player
            ) + ChatColor.YELLOW) + "."
        }
    }
}
