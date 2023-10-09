package net.revive.framework.deathmesssage.listener

import net.kyori.adventure.text.Component
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.revive.framework.utils.buildComponent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object FireDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) =
        net.revive.framework.event.event(event.player) {
            if (event.cause.cause !== EntityDamageEvent.DamageCause.FIRE_TICK && event.cause.cause !== EntityDamageEvent.DamageCause.LAVA
            ) return

            val record: List<AbstractDamage> = DeathMessageService.getDamage(event.player)
            var knocker: AbstractDamage? = null
            var knockerTime = 0L
            for (damage in record) {
                if (damage !is BurnDamage) {
                    if (damage is BurnDamageByPlayer) continue
                    if (damage !is PlayerAbstractDamage || knocker != null && damage.time <= knockerTime) continue

                    knocker = damage
                    knockerTime = damage.time
                }
            }
            if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
                event.trackerDamage = BurnDamageByPlayer(
                    event.player.uniqueId,
                    event.damage,
                    (knocker as PlayerAbstractDamage).damager
                )
            } else {
                event.trackerDamage = BurnDamage(event.player.uniqueId, event.damage)
            }
        }

    class BurnDamage(damaged: UUID, damage: Double) : AbstractDamage(damaged, damage) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(this.damaged, player)) {
                this.text(" burned to death.", Tailwind.ORANGE_400)
            }
        }
    }

    class BurnDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) : PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" burned to death thanks to ", Tailwind.ORANGE_400)
                append(wrapName(damager, player))
                text(".", Tailwind.ORANGE_400)
            }
        }
    }
}
