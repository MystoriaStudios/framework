package net.revive.framework.entity

import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.nms.entity.INMSEntityHandler
import net.revive.framework.utils.AngleUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractNMSEntity(var location: Location) {

    /**
     * The ID of this entity.
     */
    @Transient
    var id: Int = -1

    /**
     * The unique ID of this entity. Persistent across server restarts.
     */
    val uuid: UUID = UUID.randomUUID()

    @Transient
    private var initialized: Boolean = false

    /**
     * If this entity is hidden or not.
     */
    @Transient
    private var hidden: Boolean = false

    /**
     * If this entity is persistent across server restarts.
     */
    @Transient
    var persistent: Boolean = true

    /**
     * If this entity is the root entity of a multi-part entity.
     */
    @Transient
    var root: Boolean = true

    /**
     * This entity's animation.
     */
    //var animation: EntityAnimation? = null

    /**
     * The players currently viewing this entity.
     */
    @Transient
    protected lateinit var currentWatchers: MutableSet<UUID>

    val multiPartEntity: Boolean
        get() = false

    var command: String? = null

    var motionX: Double = 0.0
    var motionY: Double = 0.0
    var motionZ: Double = 0.0

    abstract fun getTypeName(): String

    open fun getChildEntities(): List<AbstractNMSEntity> = listOf()

    /**
     * Each transient field (fields that are not serialized) of an [AbstractNMSEntity] implementation
     * must be initialized in this method as GSON deserialization sets transient field values
     * to null.
     *
     * Therefore, this method is called immediately after deserialization.
     */
    open fun initializeData() {
        this.id = IEntityHandler.instance.nextEntityId()
        this.persistent = true
        this.root = true
        this.currentWatchers = ConcurrentHashMap.newKeySet()

        if (this.multiPartEntity) {
            for (childEntity in getChildEntities()) {
                childEntity.initializeData()
                childEntity.persistent = false
                childEntity.root = false
            }
        }

        this.initialized = true
    }

    open fun onDeletion() {
        destroyForCurrentWatchers()

        if (isMultiPartEntity()) {
            for (child in getChildEntities()) {
                child.destroyForCurrentWatchers()
                child.onDeletion()
            }
        }
    }

    /**
     * Updates the [Entity.location] of this entity to the given [location] and sends updates to the current watchers.
     */
    open fun updateLocation(location: Location) {
        this.location = location

        for (player in getCurrentWatcherPlayers()) {
            if (isVisibleToPlayer(player)) sendRepositionPackets(player)
        }
    }

    /**
     * Gets a prepped location that's used to spawn/reposition an entity.
     */
    open fun getAdjustedLocation(player: Player): Location {
        return location
    }

    /**
     *
     */
    open fun getDebugViewLocation(): Location {
        return location.clone().subtract(0.0, 0.0, 0.0)
    }
    /*

        open fun getAttachedHologram(): AbstractNMSHologram?
        {
            return null
        }
    */

    fun isInitialized(): Boolean {
        return initialized
    }

    /**
     * If this entity is visible.
     */
    open fun isVisible(): Boolean {
        return !hidden
    }

    /**
     * If this entity is visible to the given [player].
     */
    open fun isVisibleToPlayer(player: Player): Boolean {
        return !hidden && location.world == player.world && location.distance(player.location) <= 32.0
    }

    /**
     * Updates the visibility of this entity.
     */
    open fun updateVisibility(hidden: Boolean) {
        val hasChanged = hidden != this.hidden

        this.hidden = hidden

        if (hasChanged) {
            if (this.hidden) {
                for (player in getCurrentWatcherPlayers()) {
                    destroy(player)
                }
            } else {
                for (player in getCurrentWatcherPlayers()) {
                    spawn(player)
                }
            }
        }
    }

    /**
     * Called when the given [player] left-clicks this entity.
     */
    open fun onLeftClick(player: Player) {
    }

    /**
     * Called when the given [player] right-clicks this entity.
     */
    open fun onRightClick(player: Player) {
        if (command != null) {
            player.chat("/$command")
        }
    }

    open fun isDamageable(): Boolean {
        return false
    }

    open fun spawn(player: Player) {
        if (currentWatchers.contains(player.uniqueId)) {
            return
        }

        currentWatchers.add(player.uniqueId)

        sendSpawnPackets(player)
        sendUpdatePackets(player)

        val multiPart = isMultiPartEntity()
        if (multiPart) {
            for (childEntity in getChildEntities()) {
                childEntity.spawn(player)
            }
        }
    }

    open fun update(player: Player) {
        if (!currentWatchers.contains(player.uniqueId)) {
            return
        }

        sendUpdatePackets(player)

        if (isMultiPartEntity()) {
            for (childEntity in getChildEntities()) {
                childEntity.update(player)
            }
        }
    }

    open fun destroy(player: Player) {
        currentWatchers.remove(player.uniqueId)

        sendDestroyPackets(player)

        if (isMultiPartEntity()) {
            for (childEntity in getChildEntities()) {
                childEntity.destroy(player)
            }
        }
    }

    fun hasAnyWatchers(): Boolean {
        return currentWatchers.isNotEmpty()
    }

    /**
     * Gets a list of [Player] objects mapped from the [currentWatchers] list.
     */
    fun getCurrentWatcherPlayers(): List<Player> {
        return kotlin.runCatching { currentWatchers.mapNotNull { Bukkit.getPlayer(it) } }
            .onFailure { emptyList<Player>() }.getOrThrow()
    }

    fun isWatcher(playerUuid: UUID): Boolean {
        return currentWatchers.contains(playerUuid)
    }

    /**
     * Sends entity updates to all current watchers.
     */
    open fun updateForCurrentWatchers() {
        for (player in getCurrentWatcherPlayers()) {
            update(player)
        }
    }

    /**
     * Destroys the entity for all current watchers.
     */
    open fun destroyForCurrentWatchers() {
        for (player in getCurrentWatcherPlayers()) {
            destroy(player)
        }
    }

    open fun resendForCurrentWatchers() {
        for (player in getCurrentWatcherPlayers()) {
            destroy(player)
            spawn(player)
        }
    }

    open fun isMultiPartEntity(): Boolean {
        return false
    }

    open fun isRootOfMultiPartEntity(): Boolean {
        return root
    }

    open fun sendSpawnPackets(player: Player) {
    }

    open fun sendDestroyPackets(player: Player) {
        nmsEntityHandler.sendDestroyPackets(player, id)
    }

    open fun sendUpdatePackets(player: Player) {
    }

    fun sendStatusPacket(status: Byte) {
        nmsEntityHandler.sendStatusPacket(getCurrentWatcherPlayers(), id, status)
    }

    open fun sendRepositionPackets(player: Player) {
        nmsEntityHandler.sendEntityTeleport(
            player,
            id,
            location,
            AngleUtils.yawToBytes(location.yaw),
            AngleUtils.yawToBytes(location.pitch)
        )
    }

    companion object {
        @Inject
        lateinit var nmsEntityHandler: INMSEntityHandler
    }

}