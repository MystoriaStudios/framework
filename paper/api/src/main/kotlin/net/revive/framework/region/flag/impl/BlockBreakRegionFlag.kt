package net.revive.framework.region.flag.impl

import me.lucko.helper.Events
import net.revive.framework.annotation.region.RegionFlag
import net.revive.framework.flavor.service.Configure
import net.revive.framework.region.flag.IRegionFlag
import net.revive.framework.region.getAppliedRegions
import org.bukkit.event.block.BlockBreakEvent

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