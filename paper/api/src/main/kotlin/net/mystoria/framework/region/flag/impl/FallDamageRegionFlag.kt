package net.mystoria.framework.region.flag.impl

import me.lucko.helper.Events
import net.mystoria.framework.annotation.region.RegionFlag
import net.mystoria.framework.deathmessage.listener.FallDamageListener
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.region.flag.IRegionFlag
import net.mystoria.framework.region.getAppliedRegions
import org.bukkit.event.entity.EntityDamageEvent

@RegionFlag
object FallDamageRegionFlag : IRegionFlag {

    @Configure
    fun configure() {
        Events.subscribe(EntityDamageEvent::class.java)
            .filter {
                it.cause == EntityDamageEvent.DamageCause.FALL
            }
            .handler {
                val loc = it.entity.location

                if (loc.getAppliedRegions().any { it.hasFlag(this) }) {
                    it.isCancelled = true
                }
            }
    }
}