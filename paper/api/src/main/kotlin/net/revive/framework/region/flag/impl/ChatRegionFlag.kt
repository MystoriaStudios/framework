package net.revive.framework.region.flag.impl

import io.papermc.paper.event.player.AsyncChatEvent
import me.lucko.helper.Events
import net.revive.framework.annotation.region.RegionFlag
import net.revive.framework.flavor.service.Configure
import net.revive.framework.region.flag.IRegionFlag
import net.revive.framework.region.getAppliedRegions

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