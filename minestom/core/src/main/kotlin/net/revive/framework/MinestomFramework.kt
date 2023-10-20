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
    override var logger: Logger
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        val config = MinestomFrameworkServer.config

        return BasicFrameworkRedisConnection(
            BasicFrameworkRedisConnection.Details(
                "",
                0
            )
        )
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        val config = MinestomFrameworkServer.config

        return BasicFrameworkMongoConnection(
            BasicFrameworkMongoConnection.Details(
                "localhost",
                "framework"
            )
        )
    }
}