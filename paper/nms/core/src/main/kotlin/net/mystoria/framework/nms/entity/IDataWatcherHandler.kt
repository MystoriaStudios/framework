package net.mystoria.framework.nms.entity

interface IDataWatcherHandler
{
    fun initiateObject(entityClass: Class<*>): Any
    fun <T> register(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T)
    fun <T> set(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T)
    fun setFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any, value: Boolean)
    fun getFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any): Boolean

}