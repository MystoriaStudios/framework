package net.mystoria.framework.region.impl

import net.mystoria.framework.region.Boundary
import net.mystoria.framework.region.IRegion
import net.mystoria.framework.region.Point
import org.bukkit.World
import org.bukkit.entity.Player

class WorldRegion(
    private var world: World
) : IRegion {
    override val flags: MutableList<String> = mutableListOf()

    override fun isInside(x: Double, y: Double, z: Double, world: World): Boolean {
        return this.world.uid == world.uid
    }

    override fun getCenter(): Point {
        return Point(0.00, 0.00, 0.00)
    }

    override fun getVolume(): Double {
        return -1.00
    }

    override fun getBoundary() = Boundary.World(world)

    override fun drawWithParticle(player: Player) {
        TODO("Not yet implemented")
    }
}