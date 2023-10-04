package net.mystoria.framework.region

import net.mystoria.framework.region.flag.IRegionFlag
import org.bukkit.World
import org.bukkit.entity.Player

interface IRegion{

    val flags: MutableList<String>
    fun hasFlag(flag: IRegionFlag) = flags.contains(flag::class.simpleName)

    fun isInside(x: Double, y: Double, z: Double, world: World): Boolean
    fun getCenter(): Point
    fun getVolume(): Double
    fun getBoundary(): Boundary
    fun drawWithParticle(player: Player)

}