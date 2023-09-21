package net.mystoria.framework.controller

import net.mystoria.framework.storage.storable.IStorable
import kotlin.reflect.KClass

object FrameworkObjectControllerCache {

    val containers = mutableMapOf<KClass<out IStorable>, FrameworkObjectController<*>>()

    fun closeAll() {
        containers.forEach {
            close(it.value)
        }
    }

    private fun close(container: FrameworkObjectController<*>) {
        container.localLayerCache.forEach {
            it.value.runSafely(
                printTrace = false
            ) {
                it.value.connection.close()
            }
        }
    }

    inline fun <reified T : IStorable> findNotNull(): FrameworkObjectController<T> = find()!!

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IStorable> find(): FrameworkObjectController<T>? {
        val container = containers[T::class]
            ?: return null

        return container as FrameworkObjectController<T>
    }

    inline fun <reified T : IStorable> create(): FrameworkObjectController<T> {
        return create(T::class)
    }

    fun <T : IStorable> create(
        kClass: KClass<T>
    ): FrameworkObjectController<T> {
        val container = FrameworkObjectController(kClass)
        container.preLoadResources()

        containers[kClass] = container

        return container
    }
}