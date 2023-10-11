package net.revive.framework.nms.entity

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import net.revive.framework.nms.util.ByteUtil
import java.lang.reflect.ParameterizedType

@NMSHandler(NMSVersion.V1_20_R1)
object V1_20_R1DataWatcherHandler : IDataWatcherHandler {

    // https://wiki.vg/Entity_metadata#Armor_Stand

    /**
     *         return ((Byte) this.entityData.get(ArmorStand.DATA_CLIENT_FLAGS) & 1) != 0;
     *  So the get is like category kinda and & 1 is the byte in that
     */

    /**
     *         this.entityData.set(ArmorStand.DATA_CLIENT_FLAGS, this.setBit((Byte) this.entityData.get(ArmorStand.DATA_CLIENT_FLAGS), 4, showArms));
     *  The field  area,
     *
     *      private byte setBit(byte value, int bitField, boolean set) {
     *         if (set) {
     *             value = (byte) (value | bitField);
     *         } else {
     *             value = (byte) (value & ~bitField);
     *         }
     *
     *         return value;
     *     }
     *
     *     the client flags again
     *     the byte
     *     the boolean
     */

    override fun initiateObject(): Any {
        return SynchedEntityData(null)
        // Appears there may be an issue with class accessors.
    }

    override fun <T> register(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T & Any) {
        dataWatcher as SynchedEntityData
        getAccessor(wrapper)?.let {
            dataWatcher.define(
                it,
                value
            )
        }
    }

    override fun <T> set(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T & Any) {
        dataWatcher as SynchedEntityData
        getAccessor(wrapper)?.let {
            dataWatcher
                .set(
                    it,
                    value
                )
        }
    }

    override fun setFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any, value: Boolean) {
        dataWatcher as SynchedEntityData
        getAccessor(wrapper)?.let {
            dataWatcher
                .set(
                    it,
                    ByteUtil.setBit(
                        dataWatcher.get(it),
                        wrapper.bitFlag,
                        value
                    )
                )
        }
    }

    override fun getFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any): Boolean {
        dataWatcher as SynchedEntityData
        return (dataWatcher.get(getAccessor(wrapper)!!) as Byte).toInt() and wrapper.bitField != 0
    }

    private fun <T> getSerializer(
        wrapper: EntityDataAccessorWrapper<T>
    ): EntityDataSerializer<T>? {
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
        return getSerializer(wrapper)?.let {
            EntityDataAccessor(wrapper.bitField, it)
        }
    }
}