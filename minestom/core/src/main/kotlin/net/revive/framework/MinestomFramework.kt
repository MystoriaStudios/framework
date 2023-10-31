package net.revive.framework

import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import java.util.logging.Logger

object MinestomFramework : Framework() {

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
                "127.0.0.1",
                6379
            )
        )
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(
            BasicFrameworkMongoConnection.Details(
                "mongodb://localhost:27017",
                "framework"
            )
        )
    }
}