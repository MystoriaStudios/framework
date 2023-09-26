package net.mystoria.framework.entity.hologram

import net.mystoria.framework.entity.AbstractNMSEntity
import org.bukkit.Location
import java.util.LinkedList

abstract class AbstractNMSHologram(
    private var text: String,
    location: Location,
    var parent: AbstractNMSEntity? = null
) : AbstractNMSEntity(location)
{
    private var childHolograms: LinkedList<AbstractNMSHologram> = LinkedList()

    @Transient
    internal var witherSkullId: Int = -1

    @Transient
    internal var adjustedLocation: Location? = null

    var offset = false
}