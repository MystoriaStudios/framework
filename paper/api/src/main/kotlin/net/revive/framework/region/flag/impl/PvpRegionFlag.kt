package net.revive.framework.region.flag.impl

import me.lucko.helper.Events
import net.revive.framework.annotation.region.RegionFlag
import net.revive.framework.flavor.service.Configure
import net.revive.framework.region.flag.IRegionFlag
import net.revive.framework.region.getAppliedRegions
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

@RegionFlag
object PvpRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(EntityDamageByEntityEvent::class.java)
            .filter {
                it.entity is Player && it.damager is Player
            }
            .handler { it ->
                val regions = it.entity.location.getAppliedRegions()
                regions + it.damager.location.getAppliedRegions()

                if (regions.any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}