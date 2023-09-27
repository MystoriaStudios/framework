package net.mystoria.framework.region.impl

import net.mystoria.framework.region.Boundary
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.Point
import org.bukkit.Particle
import org.bukkit.entity.Player

class CylinderRegion(
    private var center: Point,
    private var radius: Double,
    private var height: Double
) : IRegion {

    override fun isInside(x: Double, y: Double, z: Double): Boolean {
        val dx = center.x - x
        val dz = center.z - z
        val distanceSquared = dx * dx + dz * dz

        return distanceSquared <= radius * radius && y >= center.y && y <= center.y + height
    }

    override fun getCenter(): Point = center

    override fun getVolume(): Double = Math.PI * radius * radius * height

    override fun getBoundary() = Boundary.Cylinder(center, radius, height)

    override fun drawWithParticle(player: Player) {
        val stepSize = 0.25
        val twoPi = 2 * Math.PI
        val angleStep = stepSize / radius

        // Drawing top and bottom faces
        var angle = 0.0
        while (angle <= twoPi) {
            val xOffset = radius * Math.cos(angle)
            val zOffset = radius * Math.sin(angle)
            val x = center.x + xOffset
            val z = center.z + zOffset

            player.spawnParticle(Particle.REDSTONE, x, center.y, z, 0)           // Bottom face
            player.spawnParticle(Particle.REDSTONE, x, center.y + height, z, 0)  // Top face

            angle += angleStep
        }

        // Drawing the side of the cylinder
        var y = center.y
        while (y <= center.y + height) {
            angle = 0.0
            while (angle <= twoPi) {
                val xOffset = radius * Math.cos(angle)
                val zOffset = radius * Math.sin(angle)
                val x = center.x + xOffset
                val z = center.z + zOffset

                player.spawnParticle(Particle.REDSTONE, x, y, z, 0)

                angle += angleStep
            }
            y += stepSize
        }
    }
}