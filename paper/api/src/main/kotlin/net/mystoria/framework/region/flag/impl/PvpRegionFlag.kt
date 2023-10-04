package net.mystoria.framework.region.flag.impl

import me.lucko.helper.Events
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
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
                val regions = it.entity.location.getAppliedRegions() + it.damager.location.getAppliedRegions()


                if (regions.any { it.hasFlag(BuildRegionFlag) }) {
                    it.isCancelled = true
                }
            }
    }
}