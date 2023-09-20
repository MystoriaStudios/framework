package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import org.bukkit.Bukkit
import java.util.logging.Level

object PaperFramework : Framework() {

    override fun constructNewRedisConnection() {
        TODO("Not yet implemented")
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        TODO("Not yet implemented")
    }

    override fun debug(from: String, message: String) {
        Bukkit.getLogger().log(Level.INFO, "[$from] $message")
    }
}