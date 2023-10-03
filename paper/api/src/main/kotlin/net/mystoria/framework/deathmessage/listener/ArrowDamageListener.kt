package net.mystoria.framework.deathmessage.listener

import net.mystoria.framework.annotation.Listeners
import net.mystoria.framework.deathmessage.damage.AbstractDamage
import net.mystoria.framework.deathmessage.damage.MobAbstractDamage
import net.mystoria.framework.deathmessage.damage.PlayerAbstractDamage
import net.mystoria.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import net.mystoria.framework.entity.util.EntityUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

@Listeners
object ArrowDamageListener : Listener {
    @EventHandler
    fun onEntityShootBow(event: EntityShootBowEvent) {
        if (event.entity is Player) {
            event.projectile.setMetadata(
                "ShotFromDistance",
                FixedMetadataValue(Bukkit.getPluginManager().getPlugin("Framework") ?: return, event.projectile.location)
            )
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) {
        if (event.cause !is EntityDamageByEntityEvent) return
        
        val damageByEntityEvent = event.cause
        if (damageByEntityEvent.damager !is Arrow) return
        
        val arrow = damageByEntityEvent.damager as Arrow
        if (arrow.shooter is Player) {
            val shooter = arrow.shooter as Player?
            for (value in arrow.getMetadata("ShotFromDistance")) {
                val shotFrom = value.value() as Location?
                val distance = shotFrom!!.distance(event.player.location)
                event.trackerDamage = ArrowDamageByPlayer(
                    event.player.uniqueId,
                    event.damage,
                    shooter!!.uniqueId,
                    distance
                )
            }
        } else if (arrow.shooter != null) {
            if (arrow.shooter is Entity) {
                event.trackerDamage = 
                    ArrowDamageByMob(
                        event.player.uniqueId,
                        event.damage,
                        arrow.shooter as Entity?
                    )
            }
        } else {
            event.trackerDamage = ArrowDamage(event.player.uniqueId, event.damage)
        }
    }

    class ArrowDamage(damaged: UUID, damage: Double) : AbstractDamage(damaged, damage) {
        override fun getDeathMessage(player: UUID): String {
            return (wrapName(this.damaged, player) + ChatColor.YELLOW) + " was shot."
        }
    }

    class ArrowDamageByPlayer(damaged: UUID, damage: Double, damager: UUID, private val distance: Double) :
        PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " was shot by " + wrapName(
                this.damager,
                player
            ) + ChatColor.YELLOW) + " from " + ChatColor.BLUE + distance.toInt() + " blocks" + ChatColor.YELLOW + "."
        }
    }

    class ArrowDamageByMob(damaged: UUID, damage: Double, damager: Entity?) :
        MobAbstractDamage(damaged, damage, damager!!.type) {
        override fun getDeathMessage(player: UUID): String {
            return ((wrapName(
                this.damaged,
                player
            ) + ChatColor.YELLOW) + " was shot by a " + ChatColor.RED + EntityUtils.getName(this.mobType) + ChatColor.YELLOW) + "."
        }
    }
}
