package net.revive.framework

import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.constants.Deployment
import net.revive.framework.flavor.Flavor
import net.revive.framework.interceptor.FrameworkAuthenticationInterceptor
import net.revive.framework.message.FrameworkMessageHandler
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.IPermissionRegistry
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

        runCatching {
            sentryService.configure()
        }
        /*runCatching {
            messageHandler.configure()
        }*/
        //SecurityService.configure(Argon2HashingAlgorithm)
        instance.log("Framework", "Setup securing hashing using TPM 2.1")

        /*retrofit = Retrofit.Builder()
            .baseUrl("${Deployment.Security.API_BASE_URL}/")
            .client(useWithReturn {
                it.okHttpClient
            })
            .addConverterFactory(GsonConverterFactory.create(GsonSerializer.gson))
            .build()*/
        logger.log(Level.INFO, "Registering Retrofit instance as required.")
    }

    abstract fun constructNewRedisConnection(): AbstractFrameworkRedisConnection
    abstract fun constructNewMongoConnection(): AbstractFrameworkMongoConnection

    open fun log(from: String, message: String) {
        logger.log(Level.INFO, "[$from] $message")
    }

    open fun severe(from: String, message: String) {
        logger.log(Level.SEVERE, "[$from] $message")
    }
}

fun String.debug(from: String) = Framework.use {
    it.log(from, this)
}