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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause.*
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object GeneralDamageListener : Listener {
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) =
        net.revive.framework.event.event(event.player) {
            var message: String? = null
            message = when (event.cause.cause) {
                SUFFOCATION -> "suffocated"
                DROWNING -> "drowned"
                STARVATION -> "starved to death"
                LIGHTNING -> "was struck by lightning"
                POISON -> "was poisoned"
                WITHER -> "withered away"
                CONTACT -> "was pricked to death"
                ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "was blown to smithereens"
                else -> return
            }

            val record: List<AbstractDamage> = DeathMessageService.getDamage(event.player)
            var knocker: AbstractDamage? = null
            var knockerTime = 0L
            for (damage in record) {
                if (damage !is GeneralDamage) {
                    if (damage is GeneralDamageByPlayer) continue
                    if (damage !is PlayerAbstractDamage || knocker != null && damage.time <= knockerTime) continue

                    knocker = damage
                    knockerTime = damage.time
                }
            }
            if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
                event.trackerDamage = GeneralDamageByPlayer(
                    event.player.uniqueId,
                    event.damage,
                    (knocker as PlayerAbstractDamage).damager,
                    message
                )
            } else {
                event.trackerDamage = GeneralDamage(event.player.uniqueId, event.damage, message)
            }
        }

    class GeneralDamage(damaged: UUID, damage: Double, private val message: String) : AbstractDamage(damaged, damage) {
        override fun getDeathMessage(player: UUID): String {
            return wrapName(this.damaged, player) + " " + ChatColor.YELLOW + message + "."
        }
    }

    class GeneralDamageByPlayer(damaged: UUID, damage: Double, damager: UUID, private val message: String) :
        PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): String {
            return (wrapName(
                this.damaged,
                player
            ) + " " + ChatColor.YELLOW + message + " while fighting " + wrapName(
                this.damager,
                player
            ) + ChatColor.YELLOW) + "."
        }
    }
}
