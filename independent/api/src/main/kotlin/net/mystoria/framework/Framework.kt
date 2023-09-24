package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.connection.redis.AbstractFrameworkRedisConnection
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.message.FrameworkMessageHandler
import net.mystoria.framework.sentry.SentryService
import net.mystoria.framework.serializer.IFrameworkSerializer
import net.mystoria.framework.serializer.impl.GsonSerializer
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Contains code that is not platform
 * dependent
 */
abstract class Framework {

    companion object {
        lateinit var instance: Framework

        fun supply(framework: Framework, lambda: (Framework) -> Unit) {
            instance = framework
            instance.configure()
            lambda.invoke(instance)
        }

        fun use(lambda: (Framework) -> Unit) = lambda.invoke(instance)
        fun <T> useWithReturn(lambda: (Framework) -> T) = lambda.invoke(instance)
    }

    abstract var logger: Logger
    lateinit var flavor: Flavor

    var sentryService: SentryService = SentryService()
    var messageHandler = FrameworkMessageHandler()
    var serializer: IFrameworkSerializer = GsonSerializer

    fun configure() {
        instance.log("Framework", "Registered with extension class ${this::class.simpleName}")

        sentryService.configure()
        messageHandler.configure()
    }

    abstract fun constructNewRedisConnection() : AbstractFrameworkRedisConnection
    abstract fun constructNewMongoConnection() : AbstractFrameworkMongoConnection

    fun log(from: String, message: String) {
        logger.log(Level.INFO, "[$from] $message")
    }

    fun severe(from: String, message: String) {
        logger.log(Level.SEVERE, "[$from] $message")
    }
}

fun String.debug(from: String) = Framework.use {
    it.log(from, this)
}