package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.mystoria.framework.connection.redis.AbstractFrameworkRedisConnection
import net.mystoria.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.mystoria.framework.permission.IPermissionProvider
import net.mystoria.framework.permission.IPermissionRegistry
import net.mystoria.framework.permission.PaperPermissionProvider
import net.mystoria.framework.permission.PaperPermissionRegistry
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.Bukkit

object PaperFramework : Framework() {

    var registeredKotlinPlugins = mutableListOf<ExtendedKotlinPlugin>()
    override var logger = Bukkit.getLogger()

    override var permissionProvider: IPermissionProvider<*> = PaperPermissionProvider
    override var permissionRegistry: IPermissionRegistry = PaperPermissionRegistry

    override fun constructNewRedisConnection() : AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }

    fun registerInternalPlugin(plugin: ExtendedKotlinPlugin) = registeredKotlinPlugins.add(plugin)
}