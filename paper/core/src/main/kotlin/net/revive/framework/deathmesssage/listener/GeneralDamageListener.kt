package net.revive.framework.deathmesssage.listener

import net.kyori.adventure.text.Component
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.revive.framework.utils.buildComponent
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
            var message: String = when (event.cause.cause) {
                SUFFOCATION -> "suffocated"
                DROWNING -> "drowned"
                SUICIDE -> "committed suicide"
                MAGIC -> "was killed by magic"
                FLY_INTO_WALL -> "crashed into a wall"
                HOT_FLOOR -> "stood on something hot"
                DRYOUT -> "froze to death"
                FALLING_BLOCK -> "was crushed."
                CRAMMING -> "was stuck in a tight space"
                SONIC_BOOM -> "was obliterated by a sonic boom"
                DRAGON_BREATH -> "met the foul stench of the dragon"
                STARVATION -> "starved to death"
                LIGHTNING -> "was struck by lightning"
                POISON -> "was poisoned"
                WITHER -> "withered away"
                CONTACT -> "was pricked to death"
                WORLD_BORDER -> "tried to escape the border"
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
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" $message.", Tailwind.AMBER_400)
            }
        }
    }

    class GeneralDamageByPlayer(damaged: UUID, damage: Double, damager: UUID, private val message: String) :
        PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" $message whilst fighting ", Tailwind.AMBER_400)
                append(wrapName(damager, player))
                text(".", Tailwind.AMBER_400)
            }
        }
    }
}
