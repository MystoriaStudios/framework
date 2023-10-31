package net.revive.framework

import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.generic.GenericKubernetesApi
import io.kubernetes.client.openapi.models.*
import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.mongo.impl.BasicFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.revive.framework.logger.FrameworkLogger
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import java.util.*
import java.util.logging.Logger


object BackendFramework : Framework() {
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

    init {

    }

    override fun constructNewRedisConnection(): AbstractFrameworkRedisConnection {
        return BasicFrameworkRedisConnection(BasicFrameworkRedisConnection.Details())
    }

    override fun constructNewMongoConnection(): AbstractFrameworkMongoConnection {
        return BasicFrameworkMongoConnection(BasicFrameworkMongoConnection.Details())
    }
}