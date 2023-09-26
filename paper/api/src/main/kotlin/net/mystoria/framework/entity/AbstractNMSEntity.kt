package net.mystoria.framework.entity

import org.bukkit.Location
import java.util.UUID
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

    var command: String? = null

    var motionX: Double = 0.0
    var motionY: Double = 0.0
    var motionZ: Double = 0.0

    abstract fun getTypeName(): String

    /**
     * Each transient field (fields that are not serialized) of an [Entity] implementation
     * must be initialized in this method as GSON deserialization sets transient field values
     * to null.
     *
     * Therefore, this method is called immediately after deserialization.
     */
}
    }
}