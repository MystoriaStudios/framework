package net.mystoria.framework.region

sealed class Boundary {

    data class Cuboid(val min: Point, val max: Point) : Boundary()
    data class Sphere(val center: Point, val radius: Double) : Boundary()
    data class Cylinder(val center: Point, val radius: Double, val height: Double) : Boundary()
    data class Polygon(val vertices: List<Point>, val minY: Double, val maxY: Double) : Boundary()
    data class World(val world: org.bukkit.World) : Boundary()
}
