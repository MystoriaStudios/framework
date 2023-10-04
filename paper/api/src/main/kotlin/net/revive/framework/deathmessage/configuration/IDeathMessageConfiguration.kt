package net.revive.framework.deathmessage.configuration

import net.revive.framework.cache.impl.distribution.DistributedRedisUUIDCache
import java.util.*
import org.bukkit.ChatColor as CC

interface IDeathMessageConfiguration {
    fun shouldShowDeathMessage(
        viewer: UUID,
        target: UUID,
        killer: UUID?
    ): Boolean

    fun formatPlayerName(
        player: UUID
    ): String

    fun formatPlayerName(
        player: UUID,
        viewer: UUID
    ) = this.formatPlayerName(player)

    fun hideWeapons(): Boolean = false

    companion object {
        val DEFAULT_CONFIGURATION: IDeathMessageConfiguration = object : IDeathMessageConfiguration {
            override fun shouldShowDeathMessage(
                viewer: UUID,
                target: UUID,
                killer: UUID?
            ) = true

            override fun formatPlayerName(player: UUID) = "${CC.RED}${DistributedRedisUUIDCache.username(player)}"
        }
    }
}
