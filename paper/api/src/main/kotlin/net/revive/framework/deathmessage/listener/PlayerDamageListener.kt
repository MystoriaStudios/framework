package net.revive.framework.deathmessage.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.revive.framework.event.event
import org.apache.commons.lang3.text.WordUtils
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

        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " was slain by " + wrapName(
                this.damager,
                player
            ) + ChatColor.YELLOW) + (if (DeathMessageService.configuration.hideWeapons()
            ) "" else " using " + ChatColor.RED + itemString!!.trim { it <= ' ' }) + ChatColor.YELLOW + "."
        }
    }
}
