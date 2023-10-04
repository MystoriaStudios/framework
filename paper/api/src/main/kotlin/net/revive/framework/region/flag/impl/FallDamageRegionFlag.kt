package net.revive.framework.region.flag.impl

import me.lucko.helper.Events
import net.revive.framework.annotation.region.RegionFlag
import net.revive.framework.deathmessage.listener.FallDamageListener
import net.revive.framework.flavor.service.Configure
import net.revive.framework.region.flag.IRegionFlag
import net.revive.framework.region.getAppliedRegions
import org.bukkit.event.entity.EntityDamageEvent

@RegionFlag
object FallDamageRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(EntityDamageEvent::class.java)
            .filter {
                it.cause == EntityDamageEvent.DamageCause.FALL
            }
            .handler { it ->
                val loc = it.entity.location

                if (loc.getAppliedRegions().any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}