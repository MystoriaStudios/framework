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

    override fun constructNewRedisConnection() : AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }

    fun registerInternalPlugin(plugin: ExtendedKotlinPlugin) = registeredKotlinPlugins.add(plugin)
}