package net.mystoria.framework.nms.entity

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Zombie
import net.mystoria.framework.nms.NMSVersion
import net.mystoria.framework.nms.annotation.NMSHandler

@NMSHandler(NMSVersion.V1_20_R1)
class V1_20_R1DataWatcherHandler : IDataWatcherHandler {

    private val test: EntityDataAccessor<Byte> = SynchedEntityData.defineId()

    override fun initiateObject(entityClass: Class<*>): Any {
        return SynchedEntityData(null)
        // Appears there may be an issue with class accessors.
    }

    override fun register(entityClass: Class<*>, dataWatcher: Any, key: Int, value: Any) {
        dataWatcher as SynchedEntityData
        dataWatcher.define(key, value)
        SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN)
    }

}