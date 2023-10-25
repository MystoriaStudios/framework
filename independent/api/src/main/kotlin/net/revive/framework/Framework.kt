package net.revive.framework

import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.constants.Deployment
import net.revive.framework.flavor.Flavor
import net.revive.framework.instance.Instance
import net.revive.framework.instance.InstanceService
import net.revive.framework.interceptor.FrameworkAuthenticationInterceptor
import net.revive.framework.message.FrameworkMessageHandler
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
import net.revive.framework.security.SecurityService
import net.revive.framework.security.impl.Argon2HashingAlgorithm
import net.revive.framework.sentry.SentryService
import net.revive.framework.serializer.IFrameworkSerializer
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.storage.type.FrameworkStorageType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Contains code that is not platform
 * dependent
 */
abstract class Framework {

    companion object {
        lateinit var instance: Framework

        fun supply(framework: Framework, lambda: (Framework) -> Unit) {
            instance = framework
            lambda.invoke(instance)
        }

        fun use(lambda: (Framework) -> Unit) = lambda.invoke(instance)
        fun <T> useWithReturn(lambda: (Framework) -> T) = lambda.invoke(instance)
    }

    lateinit var platform: IFrameworkPlatform
    abstract var permissionProvider: IPermissionProvider
    abstract var permissionRegistry: IPermissionRegistry

    abstract var logger: Logger
    lateinit var flavor: Flavor
    lateinit var retrofit: Retrofit

    var sentryService: SentryService = SentryService()
    var messageHandler = FrameworkMessageHandler()
    var serializer: IFrameworkSerializer = GsonSerializer

    var okHttpClient = OkHttpClient.Builder()
        .addInterceptor(FrameworkAuthenticationInterceptor)
        .build()

    fun configure(platform: IFrameworkPlatform) {
        this.platform = platform
        instance.log("Framework", "Registered with extension class ${this::class.simpleName}")

        sentryService.configure()
        messageHandler.configure()
        SecurityService.configure(Argon2HashingAlgorithm)

        retrofit = Retrofit.Builder()
            .baseUrl("${Deployment.Security.API_BASE_URL}/")
            .client(useWithReturn {
                it.okHttpClient
            })
            .addConverterFactory(GsonConverterFactory.create(GsonSerializer.gson))
            .build()
        logger.log(Level.INFO, "Registering Retrofit instance as required.")
    }

    abstract fun constructNewRedisConnection(): AbstractFrameworkRedisConnection
    abstract fun constructNewMongoConnection(): AbstractFrameworkMongoConnection

    fun log(from: String, message: String) {
        logger.log(Level.INFO, "[$from] $message")
    }

    fun severe(from: String, message: String) {
        logger.log(Level.SEVERE, "[$from] $message")
    }

    fun updateInstance() {
        InstanceService.local = InstanceService.byId(platform.id) ?: Instance.create(platform)
        InstanceService.local.provideData(platform)
        InstanceService.controller.save(InstanceService.local, FrameworkStorageType.REDIS)
    }
}

fun String.debug(from: String) = Framework.use {
    it.log(from, this)
}