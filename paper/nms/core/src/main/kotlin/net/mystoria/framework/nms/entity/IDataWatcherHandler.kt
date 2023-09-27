package net.mystoria.framework.nms.entity

interface IDataWatcherHandler
{
    fun initiateObject(entityClass: Class<*>): Any
    fun register(dataWatcher: Any, key: Int, value: Any)
}