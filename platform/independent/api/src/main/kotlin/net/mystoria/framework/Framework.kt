package net.mystoria.framework

import net.mystoria.framework.serializer.FrameworkSerializer
import net.mystoria.framework.serializer.impl.GsonSerializer
import kotlin.properties.Delegates

/**
 * Contains code that is not platform
 * dependent
 */
abstract class Framework {

    companion object {
        private var instance by Delegates.notNull<Framework>()
        var serializer: FrameworkSerializer = GsonSerializer

        fun use(lambda: (Framework) -> Unit) = lambda.invoke(instance)
    }

    abstract fun constructNewRedisConnection()
    abstract fun constructNewMongoConnection()

    abstract fun debug(from: String, message: String)
}

fun String.debug(from: String) = Framework.use {
    it.debug(from, this)
}