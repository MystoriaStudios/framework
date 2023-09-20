package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
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

        fun use(lambda: (Framework) -> Unit) = lambda.invoke(instance)
        fun <T> useWithReturn(lambda: (Framework) -> T) = lambda.invoke(instance)
    }

    var serializer: FrameworkSerializer = GsonSerializer

    abstract fun constructNewRedisConnection()
    abstract fun constructNewMongoConnection() : AbstractFrameworkMongoConnection

    abstract fun debug(from: String, message: String)
}

fun String.debug(from: String) = Framework.use {
    it.debug(from, this)
}