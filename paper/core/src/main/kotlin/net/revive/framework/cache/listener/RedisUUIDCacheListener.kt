package net.revive.framework.cache.listener

import net.revive.framework.annotation.Listeners
import net.revive.framework.cache.impl.distribution.DistributedRedisUUIDCache
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Listeners
object RedisUUIDCacheListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (DistributedRedisUUIDCache.username(event.player.uniqueId) == null) {
            DistributedRedisUUIDCache.update(event.player.name, event.player.uniqueId)
        }
    }
}