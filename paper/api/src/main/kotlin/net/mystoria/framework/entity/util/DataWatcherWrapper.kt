package net.mystoria.framework.entity.util

import net.mystoria.framework.flavor.annotation.Inject
import net.mystoria.framework.nms.entity.IDataWatcherHandler


inline fun DataWatcherEditor(builder: DataWatcherWrapper.() -> Unit) = DataWatcherWrapper().apply(builder)

class DataWatcherWrapper
{

    private var dataWatcher: Any =
        dataWatcherHandler.initiateObject()

    fun register(key: Int, value: Any) = dataWatcherHandler.register(dataWatcher, key, value)

    companion object {
        @Inject
        lateinit var dataWatcherHandler: IDataWatcherHandler
    }
}
