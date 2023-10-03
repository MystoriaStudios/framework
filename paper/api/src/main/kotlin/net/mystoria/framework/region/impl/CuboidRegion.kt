package net.mystoria.framework.region.impl

import net.mystoria.framework.region.Boundary
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.Point
import org.bukkit.Particle
import org.bukkit.entity.Player

class CuboidRegion(
    private var min: Point,
    private var max: Point
) : IRegion {

    override val flags = mutableListOf<String>()

    override fun isInside(x: Double, y: Double, z: Double): Boolean {
        return x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
    }

    override fun getCenter(): Point {
        return Point(
            (min.x + max.x) / 2,
            (min.y + max.y) / 2,
            (min.z + max.z) / 2
        )
    }

    override fun getVolume(): Double {
        val width = max.x - min.x
        val height = max.y - min.y
        val depth = max.z - min.z
        return width * height * depth
    }

    /**
     * Possibly a method to get the boundary of the region. This could be
     * represented in various ways, e.g., maximum and minimum points for a box.
     */
    override fun getBoundary(): Boundary {
        return Boundary.Cuboid(min, max)
    }

    override fun drawWithParticle(player: Player) {
        val stepSize = 0.25

        // Drawing bottom and top faces
        var x = min.x
        while (x <= max.x) {
            var z = min.z
            while (z <= max.z) {
                player.spawnParticle(Particle.REDSTONE, x, min.y, z, 0)
                player.spawnParticle(Particle.REDSTONE, x, max.y, z, 0)
                z += stepSize
            }
            x += stepSize
        }

        var y = min.y
        while (y <= max.y) {
            listOf(min.x, max.x).forEach { xCoord ->
                player.spawnParticle(Particle.REDSTONE, xCoord, y, min.z, 0)
                player.spawnParticle(Particle.REDSTONE, xCoord, y, max.z, 0)
            }

            listOf(min.z, max.z).forEach { zCoord ->
                player.spawnParticle(Particle.REDSTONE, min.x, y, zCoord, 0)
                player.spawnParticle(Particle.REDSTONE, max.x, y, zCoord, 0)
            }
            y += stepSize
        }
    }
}