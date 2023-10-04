package net.mystoria.framework.region.flag.impl

import me.lucko.helper.Events
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
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