package net.mystoria.framework.region.flag

import net.mystoria.framework.region.IRegion

interface IRegionFlag {

    fun appliesOn(region: IRegion) = region.hasFlag(this)
}