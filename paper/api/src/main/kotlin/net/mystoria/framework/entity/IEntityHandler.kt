package net.mystoria.framework.entity

import net.mystoria.framework.flavor.annotation.Inject
import org.bukkit.entity.Player
import java.util.UUID

interface IEntityHandler {

    fun nextEntityId(): Int

    fun getEntities(): Set<AbstractNMSEntity>

    fun <T : AbstractNMSEntity> getEntitiesByType(clazz: Class<out T>): Set<T>

    fun getEntityByUUID(uuid: UUID): AbstractNMSEntity?

    fun getEntityById(id: Int): AbstractNMSEntity?

    fun isEntityTracked(entity: AbstractNMSEntity): Boolean

    fun trackEntity(entity: AbstractNMSEntity)

    fun forgetEntity(entity: AbstractNMSEntity)

    fun isTrackingInstanceEntities(player: Player): Boolean

    fun startTrackingInstanceEntities(player: Player)

    fun stopTrackingInstanceEntities(player: Player): MutableSet<AbstractNMSEntity>

    fun getInstanceEntities(player: Player): MutableSet<AbstractNMSEntity>

    fun isInstanceEntityTracked(player: Player, entity: AbstractNMSEntity)

    fun trackInstanceEntity(player: Player, entity: AbstractNMSEntity)

    fun forgetInstanceEntity(player: Player, entity: AbstractNMSEntity)

    companion object {
        @Inject
        lateinit var instance: IEntityHandler
    }
}