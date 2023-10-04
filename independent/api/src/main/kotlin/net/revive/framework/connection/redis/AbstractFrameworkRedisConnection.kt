package net.revive.framework.connection.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import net.revive.framework.connection.AbstractFrameworkConnection
import kotlin.properties.Delegates

abstract class AbstractFrameworkRedisConnection : AbstractFrameworkConnection<RedisClient, StatefulRedisConnection<String, String>>() {
    private var handle by Delegates.notNull<RedisClient>()
    lateinit var connection: StatefulRedisConnection<String, String>

    private fun getAppliedResource(): StatefulRedisConnection<String, String> {
        return try {
            connection
        } catch (exception: Exception) {
            val connection = createNewConnection()
            setConnection(connection)

            this.connection = connection.connect()
            this.connection
        }
    }

    override fun getConnection() = handle

    override fun useResource(lambda: StatefulRedisConnection<String, String>.() -> Unit) {
        try {
            val applied = getAppliedResource()
            lambda.invoke(applied)
        } catch (exception: Exception) {
            if (exception.message == "Connection is closed") return

            useLogger {
                it.info(exception.stackTraceToString())
            }
        }
    }

    override fun <T> useResourceWithReturn(
        lambda: StatefulRedisConnection<String, String>.() -> T
    ): T? {
        return try {
            val applied = getAppliedResource()
            lambda.invoke(applied)
        } catch (exception: Exception) {
            if (exception.message == "Connection is closed") return null

            useLogger {
                it.severe(exception.stackTraceToString())
            }
            null
        }
    }

    override fun setConnection(connection: RedisClient) {
        handle = connection
    }

    override fun close() {
        handle.shutdownAsync().thenAccept {
            useLogger {
                it.info("Closed Redis connection")
            }
        }
    }
}