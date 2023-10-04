package net.revive.framework.region.flag.impl

import me.lucko.helper.Events
import net.revive.framework.flavor.service.Configure
import net.revive.framework.region.flag.IRegionFlag
import net.revive.framework.region.getAppliedRegions
import org.bukkit.event.player.PlayerDropItemEvent

class DropItemsRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(PlayerDropItemEvent::class.java)
            .handler {
                val loc = it.player.location

                if (loc.getAppliedRegions().any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}