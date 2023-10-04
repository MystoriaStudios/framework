package net.mystoria.framework.region.flag.impl

import me.lucko.helper.Events
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
import org.bukkit.event.block.BlockPlaceEvent

@RegionFlag
object BuildRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(BlockPlaceEvent::class.java).handler { it ->
            val block = it.block.location

            if (block.getAppliedRegions().any { it.hasFlag(BuildRegionFlag) }) {
                it.isCancelled = true
            }
        }
    }
}