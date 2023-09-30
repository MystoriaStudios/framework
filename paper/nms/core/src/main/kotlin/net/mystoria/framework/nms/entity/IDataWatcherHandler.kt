package net.mystoria.framework.nms.entity

interface IDataWatcherHandler
{
    fun initiateObject(): Any
    fun <T> register(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T & Any)
    fun <T> set(wrapper: EntityDataAccessorWrapper<T>, dataWatcher: Any, value: T & Any)
    fun setFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any, value: Boolean)
    fun getFlag(wrapper: EntityDataAccessorWrapper<Byte>, dataWatcher: Any): Boolean

}