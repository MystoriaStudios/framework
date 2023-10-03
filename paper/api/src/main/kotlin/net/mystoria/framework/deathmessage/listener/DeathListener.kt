package net.mystoria.framework.deathmessage.listener

import net.mystoria.framework.deathmessage.DeathMessageService
import net.mystoria.framework.deathmessage.damage.AbstractDamage
import net.mystoria.framework.deathmessage.damage.PlayerAbstractDamage
import net.mystoria.framework.deathmessage.damage.UnknownDamage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*
import java.util.concurrent.TimeUnit

class DeathListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeathEarly(event: PlayerDeathEvent) {
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
    fun onPlayerDeathLate(event: PlayerDeathEvent) {
        val record: List<AbstractDamage> = DeathMessageService.getDamage(event.entity)
        val deathCause: AbstractDamage = if (record.isNotEmpty()) {
            record[record.size - 1]
        } else {
            UnknownDamage(event.entity.uniqueId, 1.0)
        }

        DeathMessageService.clearDamage(event.entity)
        event.deathMessage = null
        val configuration = DeathMessageService.configuration
        val diedUuid = event.entity.uniqueId
        val killerUuid = if (event.entity.killer == null) null else event.entity.killer!!.uniqueId
        for (player in Bukkit.getServer().onlinePlayers) {
            val showDeathMessage: Boolean = configuration.shouldShowDeathMessage(player.uniqueId, diedUuid, killerUuid)
            if (showDeathMessage) {
                val deathMessage: String = deathCause.getDeathMessage(player.uniqueId)
                player.sendMessage(deathMessage)
            }
        }
    }
}
