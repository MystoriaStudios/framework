package net.revive.framework.deathmessage.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.deathmessage.damage.MobAbstractDamage
import net.revive.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.revive.framework.entity.util.EntityUtils
import net.revive.framework.event.event
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

@Listeners
object EntityDamageListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) = event(event.player) {
            if (event.cause !is EntityDamageByEntityEvent) return

            val damageByEntityEvent = event.cause
            val damager = damageByEntityEvent.damager
            if (damager is LivingEntity && damager !is Player) {
                event.trackerDamage = EntityDamage(event.player.uniqueId, event.damage, damager)
            }
        }

    class EntityDamage(damaged: UUID, damage: Double, entity: Entity) : MobAbstractDamage(damaged, damage, entity.type) {
        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " was slain by a " + ChatColor.RED + EntityUtils.getName(this.mobType) + ChatColor.YELLOW) + "."
        }
    }
}
