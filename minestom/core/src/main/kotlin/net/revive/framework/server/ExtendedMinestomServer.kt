package net.revive.framework.server

import net.minestom.server.MinecraftServer
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.annotation.container.flavor.LazyStartup
import net.revive.framework.annotation.inject.AutoBind
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.flavor.FlavorOptions
import net.revive.framework.flavor.annotation.IgnoreDependencyInjection
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.reflections.PackageIndexer
import net.revive.framework.message.FrameworkMessageHandler
import net.revive.framework.sentry.SentryService
import net.revive.framework.serializer.IFrameworkSerializer
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("DEPRECATION")
open class ExtendedMinestomServer : IConfigProvider {

    override fun getBaseFolder(): File {
        return File("TODO")
    }

    /**
     * START FLAVOR INJECTION AND HANDLING
     */
    lateinit var packageIndexer: PackageIndexer
    private lateinit var flavor: Flavor
    var trackedConfigs = mutableMapOf<JsonConfig, Any>()

    var logger: Logger = Logger.getLogger("Framework")

    private val usingFlavor = this::class.java.getAnnotation(IgnoreDependencyInjection::class.java) != null

    fun flavor() = flavor

    fun flavor(lambda: Flavor.() -> Unit) {
        if (!usingFlavor) return

        flavor.lambda()
    }

    /**
     * END FLAVOUR INJECTION AND HANDLING
     */

    fun load() {
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            logger.info("[Compatability] This version of Framework does not support java ${SystemUtils.JAVA_VERSION}!")
            logger.info("[Compatability] Please use a version higher than 11.")
            MinecraftServer.stopCleanly()
            return
        }

        this.flavor =
            Flavor.create((this)::class, FlavorOptions(logger))
        this.flavor.reflections =
            PackageIndexer(this::class, FlavorOptions(logger), listOf(this::class.java.classLoader))
        this.packageIndexer = this.flavor.reflections

        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerPreEnable>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure { throwable ->
                    logger.log(Level.WARNING, "Failed to enable container part!", throwable)
                }
            }

        flavor().binders.add(
            FlavorBinder(this@ExtendedMinestomServer::class) to this@ExtendedMinestomServer
        )

        flavor {
            bind<Logger>() to logger
        }

        this.packageIndexer
            .getTypesAnnotatedWith<JsonConfig>()
            .forEach {
                kotlin.runCatching {
                    logger.log(Level.INFO, "Trying to load config ${it.name}.")

                    val annotation = it.getAnnotation(JsonConfig::class.java)

                    val config = try {
                        load(annotation, it.kotlin)
                        logger.log(Level.INFO, "Loaded configuration from ${annotation.fileName}.")
                    } catch (exception: FileNotFoundException) {
                        logger.log(
                            Level.SEVERE,
                            "${annotation.fileName} not found, trying to save the default provider."
                        )
                        save(annotation, it.getConstructor().newInstance())
                    }

                    flavor().binders.add(
                        FlavorBinder(it::class) to config
                    )

                    if (annotation.autoSave) {
                        logger.log(Level.INFO, "Configuration for ${it.name} will now be auto saved.")
                        trackedConfigs[annotation] = config
                    }
                }.onFailure { throwable ->
                    logger.log(Level.SEVERE, "Failed to load json configuration correctly", throwable)
                }
            }
    }

    fun enable() {
        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerEnable>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure { throwable ->
                    logger.log(Level.WARNING, "Failed to enable container part!", throwable)
                }
            }
        net.revive.framework.Framework.use {
            flavor {
                bind<SentryService>() to it.sentryService
                bind<FrameworkMessageHandler>() to it.messageHandler
                bind<IFrameworkSerializer>() to it.serializer
            }
        }

        this.packageIndexer
            .getTypesAnnotatedWith<AutoBind>()
            .mapNotNull {
                it.kotlin.objectInstance ?: it.newInstance()
            }
            .forEach {
                this.flavor.binders.add(FlavorBinder(it::class) to it)
            }

        this.packageIndexer
            .getTypesAnnotatedWith<Inject>()
            .mapNotNull {
                it.kotlin.objectInstance
            }
            .forEach {
                this.flavor.inject(it)
            }

        if (this::class.java.getAnnotation(LazyStartup::class.java) == null) CompletableFuture.runAsync {
            this.flavor.startup()
        }
    }

    fun disable() {
        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerDisable>()
            .filter {
                it.declaringClass == this::class.java
            }
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to disable container part!", it)
                }
            }

        trackedConfigs.filter {
            it.key.autoSave
        }.forEach(this::save)

        this.flavor.close()
    }
}