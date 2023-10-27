package net.revive.framework

import net.revive.framework.config.MinestomConfig
import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import java.util.logging.Logger

object MinestomFramework : Framework() {

    @Inject
    lateinit var config: MinestomConfig

    override var permissionProvider: IPermissionProvider
        get() = TODO("Not yet implemented")
        set(value) {}
    override var permissionRegistry: IPermissionRegistry
        get() = TODO("Not yet implemented")
        set(value) {}

    override var logger: Logger = Logger.getLogger("Framework")

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(
            BasicFrameworkRedisConnection.Details(
                config.redisDetails.host,
                config.redisDetails.port
            )
        )
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(
            BasicFrameworkMongoConnection.Details(
                config.mongoDetails.uri,
                config.mongoDetails.database
            )
        )
    }
}