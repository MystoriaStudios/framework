package net.mystoria.framework.region.flag.impl

import me.lucko.helper.Events
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

@RegionFlag
object BlockBreakRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(BlockBreakEvent::class.java)
            .handler { it ->
                val loc = it.block.location

                if (loc.getAppliedRegions().any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}