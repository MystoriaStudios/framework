package net.revive.framework

import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import net.revive.framework.permission.PaperPermissionProvider
import net.revive.framework.permission.PaperPermissionRegistry
import net.revive.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.Bukkit

object PaperFramework : net.revive.framework.Framework() {

    var registeredKotlinPlugins = mutableListOf<ExtendedKotlinPlugin>()
    override var logger = Bukkit.getLogger()

    override var permissionProvider: IPermissionProvider = PaperPermissionProvider
    override var permissionRegistry: IPermissionRegistry = PaperPermissionRegistry

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        val config = PaperFrameworkPlugin.instance.config

        return BasicFrameworkRedisConnection(
            BasicFrameworkRedisConnection.Details(
                config.getString("backend.redis.host") ?: "127.0.0.1",
                config.getInt("backend.redis.port", 6379),
                config.getString("backend.redis.username"),
                config.getString("backend.redis.password") ?: System.getProperty("REDIS_PASSWORD")
            )
        )
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        val config = PaperFrameworkPlugin.instance.config

        return BasicFrameworkMongoConnection(
            BasicFrameworkMongoConnection.Details(
                config.getString("backend.mongo.host") ?: "localhost",
                config.getInt("backend.mongo.port", 27017),
                config.getString("backend.mongo.username") ?: "root",
                config.getString("backend.mongo.password") ?: System.getProperty("MONGODB_PASSWORD")
            )
        )
    }

    fun registerInternalPlugin(plugin: ExtendedKotlinPlugin) = registeredKotlinPlugins.add(plugin)
}