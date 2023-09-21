package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.connection.redis.AbstractFrameworkRedisConnection
import net.mystoria.framework.message.FrameworkMessageHandler
import net.mystoria.framework.serializer.IFrameworkSerializer
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

    var messageHandler = FrameworkMessageHandler()
    var serializer: IFrameworkSerializer = GsonSerializer

    abstract fun constructNewRedisConnection() : AbstractFrameworkRedisConnection
    abstract fun constructNewMongoConnection() : AbstractFrameworkMongoConnection

    abstract fun debug(from: String, message: String)
}

fun String.debug(from: String) = Framework.use {
    it.debug(from, this)
}