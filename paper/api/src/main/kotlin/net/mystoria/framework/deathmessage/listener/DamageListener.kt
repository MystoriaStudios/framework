package net.mystoria.framework.deathmessage.listener

import net.mystoria.framework.annotation.Listeners
import net.mystoria.framework.deathmessage.DeathMessageService
import net.mystoria.framework.deathmessage.damage.UnknownDamage
import net.mystoria.framework.deathmessage.damage.event.CustomPlayerDamageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

@Listeners
object DamageListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val customEvent = CustomPlayerDamageEvent(player, event)
            customEvent.trackerDamage = UnknownDamage(player.uniqueId, customEvent.damage)
            customEvent.callEvent()
            DeathMessageService.addDamage(player, customEvent.trackerDamage!!)
        }
    }
}
