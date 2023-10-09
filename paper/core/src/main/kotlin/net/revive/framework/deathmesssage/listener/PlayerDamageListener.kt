package net.revive.framework.deathmesssage.listener

import net.kyori.adventure.text.Component
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.revive.framework.event.event
import net.revive.framework.utils.buildComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

@Listeners
object PlayerDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) = event(event.player) {
            if (event.cause !is EntityDamageByEntityEvent) {
                return
            }
            val damageByEntityEvent = event.cause as EntityDamageByEntityEvent
            val damager = damageByEntityEvent.damager
            if (damager is Player) {
                val damaged: Player = event.player
                event.trackerDamage = PvPDamage(damaged.uniqueId, event.damage, damager)
            }
        }

    class PvPDamage(damaged: UUID, damage: Double, damager: Player) : PlayerAbstractDamage(damaged, damage, damager.uniqueId) {
        private var itemString: String? = null

        init {
            val hand = damager.itemInHand
            if (hand.type == Material.AIR) {
                itemString = "their fists"
            } else if (hand.itemMeta.hasDisplayName()) {
                itemString = hand.itemMeta.displayName
            } else {
                itemString = hand.type.name.replace('_', ' ').capitalize()
            }
        }

        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" was slain by ", Tailwind.ORANGE_400)
                append(wrapName(damager, player))
                if (!DeathMessageService.configuration.hideWeapons()) {
                    text(" using ", Tailwind.ORANGE_400)
                    text(itemString!!.trim { it <= ' ' }, Tailwind.RED_400)
                }
                text(".", Tailwind.ORANGE_400)
            }
        }
    }
}
