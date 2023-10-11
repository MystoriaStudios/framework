package net.revive.framework.deathmesssage.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.deathmessage.DeathMessageService
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.deathmessage.damage.PlayerAbstractDamage
import net.revive.framework.deathmessage.damage.UnknownDamage
import net.revive.framework.event.event
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*
import java.util.concurrent.TimeUnit

@Listeners
object DeathListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeathEarly(event: PlayerDeathEvent) = event(event.entity) {
        val record: List<AbstractDamage> = DeathMessageService.getDamage(event.entity)
        if (record.isNotEmpty()) {
            val deathCause: AbstractDamage = record[record.size - 1]
            if (deathCause is PlayerAbstractDamage && deathCause.timeAgoMillis < TimeUnit.MINUTES.toMillis(1L)) {
                val killerUuid: UUID = deathCause.damager
                val killerPlayer = Bukkit.getServer().getPlayer(killerUuid)
                if (killerPlayer != null) {
                    event.entity.killer = killerPlayer
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeathLate(event: PlayerDeathEvent) = event(event.entity) {
        val record: List<AbstractDamage> = DeathMessageService.getDamage(event.entity)
        val deathCause: AbstractDamage = if (record.isNotEmpty()) {
            record[record.size - 1]
        } else {
            UnknownDamage(event.entity.uniqueId, 1.0)
        }

        DeathMessageService.clearDamage(event.entity)
        //event.deathMessage(null)
        val configuration = DeathMessageService.configuration
        val diedUuid = event.entity.uniqueId
        val killerUuid = if (event.entity.killer == null) null else event.entity.killer!!.uniqueId
        for (player in Bukkit.getServer().onlinePlayers) {
            val showDeathMessage: Boolean = configuration.shouldShowDeathMessage(player.uniqueId, diedUuid, killerUuid)
            if (showDeathMessage) {
                val deathMessage = deathCause.getDeathMessage(player.uniqueId)
                player.sendMessage(deathMessage)
            }
        }
    }
}
