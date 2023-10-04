package net.revive.framework.region.flag

import net.revive.framework.region.IRegion

interface IRegionFlag {

    fun appliesOn(region: IRegion) = region.hasFlag(this)
}