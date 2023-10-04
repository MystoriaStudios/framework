package net.revive.framework.region

import net.revive.framework.flavor.annotation.Inject
import org.bukkit.Location
import org.bukkit.entity.Player

interface IRegionHandler {
    fun getAllAppliedRegions(player: Player): List<IRegion>

    fun getAllAppliedRegions(location: Location): List<IRegion>

    companion object {

        @Inject
        lateinit var regionHandler: IRegionHandler
    }
}