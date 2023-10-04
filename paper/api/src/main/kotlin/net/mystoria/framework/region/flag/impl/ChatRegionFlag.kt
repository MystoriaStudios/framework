package net.mystoria.framework.region.flag.impl

import io.papermc.paper.event.player.AsyncChatEvent
import me.lucko.helper.Events
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
import org.bukkit.event.player.AsyncPlayerChatEvent

@RegionFlag
object ChatRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(AsyncChatEvent::class.java)
            .handler { it ->
                val loc = it.player.location

                if (loc.getAppliedRegions().any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}