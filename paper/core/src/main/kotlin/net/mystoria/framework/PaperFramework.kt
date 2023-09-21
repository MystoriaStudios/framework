package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.mystoria.framework.connection.Redis.AbstractFrameworkRedisConnection
import net.mystoria.framework.connection.Redis.impl.BasicFrameworkRedisConnection
import org.bukkit.Bukkit

object PaperFramework : Framework() {

    override var logger = Bukkit.getLogger()

    override fun constructNewRedisConnection() : AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }
}