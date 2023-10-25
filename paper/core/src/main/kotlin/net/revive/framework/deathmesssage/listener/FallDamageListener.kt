package net.revive.framework.deathmesssage.listener

import net.kyori.adventure.text.Component
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.event.CustomPlayerDamageEvent
import net.revive.framework.event.event
import net.revive.framework.sender.PaperFrameworkPlayer
import net.revive.framework.utils.buildComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object FallDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) = event(event.player) {
        if (event.cause.cause !== EntityDamageEvent.DamageCause.FALL) {
            return
        }
        val record: List<AbstractDamage> = DeathMessageService.getDamage(PaperFrameworkPlayer(event.player))
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
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" hit the ground too hard.", Tailwind.AMBER_400)
            }
        }
    }

    class FallDamageByPlayer(damaged: UUID, damage: Double, damager: UUID) :
        PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" hit the ground too hard thanks to ", Tailwind.AMBER_400)
                append(wrapName(damager, player))
                text(".", Tailwind.AMBER_400)
            }
        }
    }
}
