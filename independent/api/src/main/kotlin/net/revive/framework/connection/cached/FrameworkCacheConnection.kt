package net.revive.framework.connection.cached

import net.revive.framework.connection.AbstractFrameworkConnection
import java.util.concurrent.ConcurrentHashMap

class FrameworkCacheConnection<K, V>(
    val handle: ConcurrentHashMap<K, V> = ConcurrentHashMap()
) : AbstractFrameworkConnection<ConcurrentHashMap<K, V>, ConcurrentHashMap<K, V>>() {

    override fun useResource(lambda: ConcurrentHashMap<K, V>.() -> Unit) = lambda.invoke(handle)
    override fun <T> useResourceWithReturn(lambda: ConcurrentHashMap<K, V>.() -> T) = lambda.invoke(handle)
    override fun getConnection(): ConcurrentHashMap<K, V> = handle
    override fun setConnection(connection: ConcurrentHashMap<K, V>) = throw IllegalCallerException("You cannot set the connection of a cached storage layer.")

    override fun createNewConnection(): ConcurrentHashMap<K, V> {
        val newMap = ConcurrentHashMap<K, V>()
        close()
        return newMap
    }

    override fun close() = handle.clear()
}