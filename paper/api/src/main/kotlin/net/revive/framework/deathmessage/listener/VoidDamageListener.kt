package net.revive.framework.deathmessage.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object VoidDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause.cause !== EntityDamageEvent.DamageCause.VOID) {
            return
        }
        val record: List<AbstractDamage> = DeathMessageService.getDamage(event.player)
        var knocker: AbstractDamage? = null
        var knockerTime = 0L
        for (damage in record) {
            if (damage !is VoidDamage) {
                if (damage is VoidDamageByPlayer) {
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
            event.trackerDamage = VoidDamageByPlayer(
                event.player.uniqueId,
                event.damage,
                (knocker as PlayerAbstractDamage).damager
            )
        } else {
            event.trackerDamage = VoidDamage(event.player.uniqueId, event.damage)
        }
    }

    class VoidDamage(damaged: UUID, damage: Double) : AbstractDamage(damaged, damage) {
        override fun getDeathMessage(player: UUID): String {
            return (wrapName(this.damaged, player) + ChatColor.YELLOW) + " fell into the void."
        }
    }

    class VoidDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) : PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " fell into the void thanks to " + wrapName(
                this.damager,
                player
            ) + ChatColor.YELLOW) + "."
        }
    }
}
