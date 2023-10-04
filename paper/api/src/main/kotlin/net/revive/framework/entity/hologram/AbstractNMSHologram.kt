package net.revive.framework.entity.hologram

import net.revive.framework.entity.AbstractNMSEntity
import net.revive.framework.entity.IEntityHandler
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

open class AbstractNMSHologram(
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

    override fun getTypeName() = "Hologram"

    override fun initializeData() {
        super.initializeData()

        this.witherSkullId = IEntityHandler.instance.nextEntityId()
    }


    override fun onLeftClick(player: Player) {
        if (parent != null) {
            parent!!.onLeftClick(player)
        }
    }

    override fun onRightClick(player: Player) {
        if (parent != null) {
            parent!!.onRightClick(player)
        }
    }

    override fun updateLocation(location: Location) {
        super.updateLocation(location)

        synchronizeLocations()
    }

    override fun getAdjustedLocation(player: Player): Location {
        return (adjustedLocation ?: location).clone()
//        return (adjustedLocation ?: location).clone().also { location ->
//            if (offset) {
//                location.add(0.0, 0.4, 0.0)
//            }
//        }
    }

    fun getFloatingItemLocation(): Location {
        return if (childHolograms.isEmpty()) {
            location.clone().add(0.0, 1.0, 0.0)
        } else {
            location.clone().add(0.0, childHolograms.size * 0.24, 0.0).add(0.0, 1.0, 0.0)
        }
    }

    override fun getDebugViewLocation(): Location {
        return if (childHolograms.isEmpty()) {
            location.clone().subtract(0.0, 0.24, 0.0)
        } else {
            childHolograms.last.location.clone().subtract(0.0, 0.24, 0.0)
        }
    }

    private fun synchronizeLocations() {
        val startLocation = location.clone().add(0.0, childHolograms.size * 0.24, 0.0)

        val originalLocation: Location? = adjustedLocation?.clone()
        adjustedLocation = startLocation.clone()

        // we will need to send reposition packet if adjustedLocation has changed
        if (originalLocation != adjustedLocation) {
            for (watcher in getCurrentWatcherPlayers()) {
                sendRepositionPackets(watcher)
            }
        }

        if (childHolograms.isEmpty()) {
            adjustedLocation = null
        }

        for (childHologram in childHolograms) {
            childHologram.parent = this
            childHologram.updateLocation(startLocation.subtract(0.0, 0.24, 0.0).clone()) // each iteration subtracts 0.24 from startLoc
        }
    }

    override fun spawn(player: Player) {
        if (!text.equals("{empty}", true) || ChatColor.stripColor(text)!!.isNotEmpty()) {
            super.spawn(player)
        }
    }

    override fun destroy(player: Player) {
        if (!text.equals("{empty}", true) || ChatColor.stripColor(text)!!.isNotEmpty()) {
            super.destroy(player)
        }
    }

    override fun sendSpawnPackets(player: Player) {
        HololgramProtocol.sendSpawnPackets(player, this)
    }

    override fun sendDestroyPackets(player: Player) {
        //val destroyPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityDestroy")
        //Reflection.setDeclaredFieldValue(destroyPacket, "a", intArrayOf(id, witherSkullId))

        //MinecraftProtocol.send(player, destroyPacket)
    }

    override fun sendUpdatePackets(player: Player) {
//        HologramProtocol.sendUpdatePackets(player, this)
    }

    override fun sendRepositionPackets(player: Player) {
  //      MinecraftProtocol.send(player, MinecraftProtocol.buildEntityTeleportPacket(id, getAdjustedLocation(player)))
    }

    override fun isMultiPartEntity(): Boolean {
        return childHolograms.isNotEmpty()
    }

    override fun getChildEntities(): List<AbstractNMSEntity> {
        return mutableListOf<AbstractNMSEntity>().also { set ->
            set.addAll(childHolograms)
        }
    }

    fun removeChildHologram(child: AbstractNMSHologram) {
        if (childHolograms.remove(child)) {
            synchronizeLocations()
        }
    }

    fun getText(): String {
        return text
    }

    fun updateText(text: String) {
        this.text = text
        updateForCurrentWatchers()
    }

    open fun getLines(): List<String> {
        return arrayListOf<String>().also { list ->
            list.add(this.text)
            list.addAll(childHolograms.map { it.getText() })
        }
    }

    fun getMappedLines(): Map<AbstractNMSHologram, String> {
        return hashMapOf<AbstractNMSHologram, String>().also { map ->
            map[this] = this.getText()

            for (child in childHolograms) {
                map[child] = child.getText()
            }
        }
    }

    fun getLinesRaw(): List<String> {
        return childHolograms.map { it.getText() }
    }

    open fun updateLines(lines: List<String>) {
        if (lines.isEmpty()) {
            throw IllegalStateException("Hologram must have at least 1 line")
        }

        updateText(lines.first())

        if (lines.size > 1) {
            updateLinesRaw(lines.drop(1))
        } else {
            updateLinesRaw(listOf())
        }
    }

    private fun updateLinesRaw(lines: List<String>) {
        val addedChildren = arrayListOf<AbstractNMSHologram>()
        val removedChildren = arrayListOf<AbstractNMSHologram>()

        when {
            lines.isEmpty() -> {
                removedChildren.addAll(childHolograms)
                childHolograms.clear()
            }
            lines.size >= childHolograms.size -> {
                lines.forEachIndexed { index, line ->
                    if (index < childHolograms.size) {
                        childHolograms[index].updateText(line)
                    } else {
                        val newChildHologram = AbstractNMSHologram(text = line, parent = this@AbstractNMSHologram, location = location)
                        newChildHologram.initializeData()
                        newChildHologram.persistent = false
                        newChildHologram.root = false
                        newChildHologram.offset = offset

                        childHolograms.add(newChildHologram)
                        addedChildren.add(newChildHologram)
                    }
                }
            }
            lines.size < childHolograms.size -> {
                lines.forEachIndexed { index, line ->
                    childHolograms[index].updateText(line)
                }

                for (i in 0..childHolograms.size - lines.size) {
                    removedChildren.add(childHolograms.last())
                    childHolograms = LinkedList(childHolograms.dropLast(1))
                }
            }
        }

        synchronizeLocations()

        for (player in getCurrentWatcherPlayers()) {
            for (child in addedChildren) {
                child.spawn(player)
            }

            for (child in removedChildren) {
                child.destroy(player)
            }
        }
    }

    open fun processPlaceholders(player: Player, text: String): String {
        /*
        if (parent != null) {
            if (parent is AbstractNMSHologram) {
                return (parent as AbstractNMSHologram).processPlaceholders(player, text)
            } else if (parent is NpcEntity) {
                if ((parent as NpcEntity).hologram != this) {
                    return (parent as NpcEntity).hologram.processPlaceholders(player, text)
                }
            }
        }
        TODO:
         */

        return text
            .replace("{playerName}", player.name)
            .replace("{playerDisplayName}", player.displayName)
    }
}
