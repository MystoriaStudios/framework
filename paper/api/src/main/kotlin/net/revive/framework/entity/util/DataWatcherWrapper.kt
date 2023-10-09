package net.revive.framework.entity.util

import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.nms.entity.EntityDataAccessorWrapper
import net.revive.framework.nms.entity.IDataWatcherHandler


inline fun dataWatcherEditor(builder: DataWatcherWrapper.() -> Unit) = DataWatcherWrapper().apply(builder)

class DataWatcherWrapper {

    private var dataWatcher: Any = dataWatcherHandler.initiateObject()

    fun register(key: Int, value: Any) = dataWatcherHandler.register(getAccessor(key), dataWatcher, value)
    operator fun set(key: Int, value: Any) = dataWatcherHandler.register(getAccessor(key), dataWatcher, value)
    fun flag(bitField: Int, bitFlag: Int, flag: Boolean) = dataWatcherHandler.getFlag(getAccessor(bitField, bitFlag), flag)

    private inline fun <reified T> getAccessor(key: Int): EntityDataAccessorWrapper<T> {
        return EntityDataAccessorWrapper.of(key)
    }

    private inline fun <reified T> getAccessor(bitField: Int, bitFlag: Int): EntityDataAccessorWrapper<T> {
        return EntityDataAccessorWrapper.of(bitField, bitFlag)
    }

    companion object {
        @Inject
        lateinit var dataWatcherHandler: IDataWatcherHandler
    }
}