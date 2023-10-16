package net.revive.framework

import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.revive.framework.logger.FrameworkLogger
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import java.util.logging.Logger

object IndependentFramework : Framework() {
    override var logger: Logger = FrameworkLogger()

    override var permissionProvider: IPermissionProvider
        get() = TODO("Not yet implemented")
        set(value) {
            println(value)
        }
    override var permissionRegistry: IPermissionRegistry
        get() = TODO("Not yet implemented")
        set(value) {
            println(value)
        }

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }
}