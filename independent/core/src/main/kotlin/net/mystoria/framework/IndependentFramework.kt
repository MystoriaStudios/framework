package net.mystoria.framework

import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.mystoria.framework.connection.redis.AbstractFrameworkRedisConnection
import net.mystoria.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.mystoria.framework.logger.FrameworkLogger
import net.mystoria.framework.permission.IPermissionProvider
import net.mystoria.framework.permission.IPermissionRegistry
import java.util.logging.Logger

object IndependentFramework : Framework() {
    override var logger: Logger = FrameworkLogger()

    override var permissionProvider: IPermissionProvider<*>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var permissionRegistry: IPermissionRegistry
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }
}