package net.revive.framework.deathmesssage.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.UnknownDamage
import net.revive.framework.event.CustomPlayerDamageEvent
import net.revive.framework.event.event
import net.revive.framework.sender.PaperFrameworkPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

@Listeners
object DamageListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) = event(event.entity) {
        if (event.entity is Player) {
            val player = event.entity as Player
            val customEvent = CustomPlayerDamageEvent(player, event)
            customEvent.trackerDamage = UnknownDamage(player.uniqueId, customEvent.damage)
            customEvent.callEvent()
            DeathMessageService.addDamage(PaperFrameworkPlayer(player), customEvent.trackerDamage!!)
        }
    }
}
