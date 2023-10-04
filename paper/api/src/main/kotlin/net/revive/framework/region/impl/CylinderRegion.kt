package net.revive.framework.region.impl

import net.revive.framework.region.Boundary
import net.revive.framework.region.IRegion
import net.revive.framework.region.Point
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player

class CylinderRegion(
    private var center: Point,
    private var radius: Double,
    private var height: Double,
    private var world: World
) : IRegion {

    override val flags = mutableListOf<String>()

    override fun isInside(x: Double, y: Double, z: Double, world: World): Boolean {
        if (this.world.uid != world.uid) return false

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