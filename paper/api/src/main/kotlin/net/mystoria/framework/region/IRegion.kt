package net.mystoria.framework.region

import org.bukkit.World
import org.bukkit.entity.Player

interface IRegion {
    fun isInside(x: Double, y: Double, z: Double): Boolean
    fun getCenter(): Point
    fun getVolume(): Double
    fun getBoundary(): Boundary
    fun drawWithParticle(player: Player)
}