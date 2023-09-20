package net.mystoria.framework.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import net.mystoria.framework.connection.AbstractFrameworkConnection
import kotlin.properties.Delegates

abstract class AbstractFrameworkMongoConnection : AbstractFrameworkConnection<MongoClient, MongoDatabase>() {
    internal var handle by Delegates.notNull<MongoClient>()

    abstract fun getAppliedResource(): MongoDatabase

    override fun useResource(lambda: MongoDatabase.() -> Unit) {
        try {
            val applied = getAppliedResource()
            lambda.invoke(applied)
        } catch (exception: Exception) {
            useLogger {
                it.severe(exception.stackTraceToString())
            }
        }
    }

    override fun <T> useResourceWithReturn(lambda: MongoDatabase.() -> T): T = lambda.invoke(getAppliedResource())


    override fun setConnection(connection: MongoClient) {
        handle = connection
    }

    override fun close() = handle.close()
}