package net.mystoria.framework.region.impl

import net.mystoria.framework.region.Boundary
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.Point
import org.bukkit.entity.Player

class SphereRegion(
    private var center: Point,
    private var radius: Double
) : IRegion {

    override fun isInside(x: Double, y: Double, z: Double): Boolean {
        val dx = center.x - x
        val dy = center.y - y
        val dz = center.z - z
        val distanceSquared = dx * dx + dy * dy + dz * dz

        return distanceSquared <= radius * radius
    }

    override fun getCenter(): Point = center

    override fun getVolume(): Double = (4.0/3.0) * Math.PI * radius * radius * radius

    override fun getBoundary(): Boundary = Boundary.Sphere(center, radius)

    override fun drawWithParticle(player: Player) {
        TODO("Not yet implemented")
    }
}