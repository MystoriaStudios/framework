package net.mystoria.framework.nms.entity

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Zombie
import net.mystoria.framework.nms.NMSVersion
import net.mystoria.framework.nms.annotation.NMSHandler
import java.lang.reflect.ParameterizedType

@NMSHandler(NMSVersion.V1_20_R1)
object V1_20_R1DataWatcherHandler : IDataWatcherHandler {

    override fun initiateObject(entityClass: Class<*>): Any {
        return SynchedEntityData(null)
        // Appears there may be an issue with class accessors.
    }

    override fun <T> register(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T) {
        dataWatcher as SynchedEntityData
        getAccessor(wrapper)?.let {
            dataWatcher.define(
                it,
                value
            )
        }
    }

    private fun <T> getSerializer(wrapper: EntityDataAccessorWrapper<T>): EntityDataSerializer<T>?
    {
        var foundSerializer: EntityDataSerializer<T>? = null
        EntityDataSerializers::class.java.declaredFields
            .filter { it.type == EntityDataSerializer::class.java }
            .filter { it.genericType is ParameterizedType }
            .forEach { field ->
                val genericType = field.genericType as ParameterizedType
                val actualType = genericType.actualTypeArguments.firstOrNull()

                if (actualType == wrapper.typeClass) {
                    field.isAccessible = true
                    foundSerializer = field.get(null) as? EntityDataSerializer<T>
                    return@forEach
                }
            }

        return foundSerializer
    }

    private fun <T> getAccessor(
        wrapper: EntityDataAccessorWrapper<T>
    ): EntityDataAccessor<T>? {
        return getSerializer<T>(wrapper)?.let {
            SynchedEntityData.defineId(wrapper.entityClass as Class<out Entity>, it)
        }
    }
}