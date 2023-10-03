package net.mystoria.framework.region.impl

import net.mystoria.framework.region.Boundary
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.Point
import org.bukkit.Particle
import org.bukkit.entity.Player

class PolygonRegion(
    private val vertices: List<Point>,
    private val minY: Double,
    private val maxY: Double
) : IRegion {

    override val flags = mutableListOf<String>()

    override fun isInside(x: Double, y: Double, z: Double): Boolean {
        if (y < minY || y > maxY) return false  // Check vertical boundaries

        // Use ray-casting algorithm to determine if the point is inside the polygon
        var intersections = 0
        for (i in vertices.indices) {
            val j = (i + 1) % vertices.size
            val xi = vertices[i].x
            val zi = vertices[i].z
            val xj = vertices[j].x
            val zj = vertices[j].z
            if ((zi > z && zj <= z) || (zj > z && zi <= z)) {
                if (x < (xj - xi) * (z - zi) / (zj - zi) + xi) {
                    intersections++
                }
            }
        }
        return intersections % 2 != 0
    }

    override fun getCenter(): Point {
        val avgX = vertices.map { it.x }.average()
        val avgZ = vertices.map { it.z }.average()
        return Point(avgX, (minY + maxY) / 2, avgZ)
    }

    override fun getVolume(): Double {
        // Since it's 2D, it's better to return area
        var area = 0.0
        for (i in vertices.indices) {
            val j = (i + 1) % vertices.size
            area += (vertices[j].x + vertices[i].x) * (vertices[j].z - vertices[i].z)
        }
        return area / 2.0
    }

    override fun getBoundary(): Boundary = Boundary.Polygon(vertices, minY, maxY)

    override fun drawWithParticle(player: Player) {
        TODO("Not yet implemented")
    }
}