package net.revive.framework.region

import org.bukkit.Location

fun Location.getAppliedRegions(): List<IRegion> = IRegionHandler.regionHandler.getAllAppliedRegions(this)