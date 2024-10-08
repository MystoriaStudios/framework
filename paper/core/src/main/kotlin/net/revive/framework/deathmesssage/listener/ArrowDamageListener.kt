package net.revive.framework.deathmesssage.listener

import net.kyori.adventure.text.Component
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.MobAbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.entity.util.EntityUtils
import net.revive.framework.event.CustomPlayerDamageEvent
import net.revive.framework.event.event
import net.revive.framework.utils.buildComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
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
    fun onEntityShootBow(event: EntityShootBowEvent) = event(event.entity) {
        if (event.entity is Player) {
            event.projectile.setMetadata(
                "ShotFromDistance",
                FixedMetadataValue(
                    Bukkit.getPluginManager().getPlugin("Framework") ?: return,
                    event.projectile.location
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onCustomPlayerDamage(event: CustomPlayerDamageEvent) = event(event.player) {
        if (event.cause !is EntityDamageByEntityEvent) return

        val damageByEntityEvent = event.cause as EntityDamageByEntityEvent
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
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" was shot.", Tailwind.AMBER_400)
            }
        }
    }

    class ArrowDamageByPlayer(damaged: UUID, damage: Double, damager: UUID, private val distance: Double) :
        PlayerAbstractDamage(damaged, damage, damager) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" was shot by ", Tailwind.AMBER_400)
                append(wrapName(damager, player))
                text(" from ", Tailwind.AMBER_400)
                text("${distance.toInt()} blocks", Tailwind.TEAL_300)
                text(".", Tailwind.AMBER_400)
            }
        }
    }

    class ArrowDamageByMob(damaged: UUID, damage: Double, damager: Entity?) :
        MobAbstractDamage(damaged, damage, damager!!.type.name) {
        override fun getDeathMessage(player: UUID): Component {
            return buildComponent(wrapName(damaged, player)) {
                text(" was shot by a ", Tailwind.AMBER_400)
                text(EntityUtils.getName(EntityType.valueOf(mobType)), Tailwind.TEAL_300)
                text(".", Tailwind.AMBER_400)
            }

        }
    }
}
