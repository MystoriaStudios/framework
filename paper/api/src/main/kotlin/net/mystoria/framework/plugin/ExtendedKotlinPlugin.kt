package net.mystoria.framework.plugin

import co.aikar.commands.BaseCommand
import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.PaperCommandManager
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.mystoria.framework.Framework
import net.mystoria.framework.annotation.Listeners
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.annotation.command.ManualRegister
import net.mystoria.framework.annotation.container.ContainerDisable
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.annotation.container.ContainerPreEnable
import net.mystoria.framework.annotation.container.flavor.LazyStartup
import net.mystoria.framework.annotation.retrofit.RetrofitService
import net.mystoria.framework.annotation.retrofit.UsesRetrofit
import net.mystoria.framework.command.FrameworkCommandManager
import net.mystoria.framework.constants.Deployment
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorBinder
import net.mystoria.framework.flavor.FlavorOptions
import net.mystoria.framework.flavor.annotation.IgnoreDependencyInjection
import net.mystoria.framework.message.FrameworkMessageHandler
import net.mystoria.framework.plugin.event.KotlinPluginEnableEvent
import net.mystoria.framework.sentry.SentryService
import net.mystoria.framework.serializer.IFrameworkSerializer
import net.mystoria.framework.serializer.impl.GsonSerializer
import net.mystoria.framework.utils.Strings
import net.mystoria.framework.utils.Tasks
import net.mystoria.framework.utils.objectInstance
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.full.hasAnnotation

/**
 * An extension of [ExtendedJavaPlugin] with
 * support for our custom annotation-based
 * plugin framework.
 */
open class ExtendedKotlinPlugin : ExtendedJavaPlugin() {

    /**
     * START FLAVOR INJECTION AND HANDLING
     */
    val packageIndexer by lazy {
        this.flavor.reflections
    }

    private lateinit var flavor: Flavor

    private val usingFlavor = this::class.java.getAnnotation(IgnoreDependencyInjection::class.java) != null

    fun flavor() = flavor

    fun flavor(lambda: Flavor.() -> Unit) {
        if (!usingFlavor) return

        flavor.lambda()
    }
    /**
     * END FLAVOUR INJECTION AND HANDLING
     */

    lateinit var commandManager: FrameworkCommandManager
    lateinit var retrofit: Retrofit

    override fun load() {
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            logger.info("[Compatability] This version of ${description.name} does not support java ${SystemUtils.JAVA_VERSION}!")
            logger.info("[Compatability] Please use a version higher than 11.")
            Bukkit.shutdown()
            return
        }

        this.flavor = Flavor.create(this::class, FlavorOptions(logger))

        if (this::class.hasAnnotation<UsesRetrofit>()) {
            retrofit = Retrofit.Builder()
                .baseUrl("${Deployment.Security.API_BASE_URL}/${this.description.name}/")
                .client(Framework.useWithReturn {
                    it.okHttpClient
                })
                .addConverterFactory(GsonConverterFactory.create(GsonSerializer.gson))
                .build()
            logger.log(Level.INFO, "Registering Retrofit instance as required.")

            this.packageIndexer
                .getTypesAnnotatedWith<RetrofitService>()
                .forEach {
                    flavor().binders.add(FlavorBinder(it::class) to retrofit.create(it))
                    logger.log(Level.INFO, "Registered Retrofit service from class ${it.name}")
                }
        }

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
            FlavorBinder(this@ExtendedKotlinPlugin::class) to this@ExtendedKotlinPlugin
        )

        flavor {
            bind<ExtendedKotlinPlugin>() to this@ExtendedKotlinPlugin
            bind<ExtendedJavaPlugin>() to this@ExtendedKotlinPlugin
            bind<JavaPlugin>() to this@ExtendedKotlinPlugin

            bind<Server>() to server
            bind<Logger>() to logger
        }
    }

    override fun enable() {
        this.packageIndexer
            .getMethodsAnnotatedWith<ContainerEnable>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this)
                }.onFailure { throwable ->
                    logger.log(Level.WARNING, "Failed to enable container part!", throwable)
                }
            }
        Framework.use {
            flavor {
                bind<SentryService>() to it.sentryService
                bind<FrameworkMessageHandler>() to it.messageHandler
                bind<IFrameworkSerializer>() to it.serializer
            }
        }

        this.commandManager = FrameworkCommandManager(this)

        flavor {
            bind<FrameworkCommandManager>() to commandManager
            bind<BukkitCommandManager>() to commandManager
            bind<PaperCommandManager>() to commandManager
        }

        var commands = 0
        this.packageIndexer
            .getTypesAnnotatedWith<AutoRegister>()
            .forEach {
                kotlin.runCatching {
                    val instance = it.objectInstance() ?: it.newInstance()

                    this.flavor.inject(instance)

                    this.commandManager.registerCommand(instance as BaseCommand)
                    commands++
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to register command", it)
                }
            }
        Framework.use {
            it.log("Commands", "Registered $commands ${Strings.pluralize(commands, "command")}")
        }

        this.packageIndexer
            .getMethodsAnnotatedWith<ManualRegister>()
            .forEach {
                kotlin.runCatching {
                    it.invoke(this, this.commandManager)
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to manually register command", it)
                }
            }

        this.packageIndexer
            .getTypesAnnotatedWith<Listeners>()
            .mapNotNull {
                it.kotlin.objectInstance
            }
            .forEach {
                this.flavor.inject(it)

                this.server.pluginManager.registerEvents(it as Listener, this)
            }

        if (this::class.java.getAnnotation(LazyStartup::class.java) == null) Tasks.async {
            this.flavor.startup()
        }

        if (!KotlinPluginEnableEvent(this).callEvent()) {
            this.logger.severe("Disabling plugin due to event being cancelled by another plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    override fun disable() {
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

        this.commandManager.unregisterCommands()
        this.flavor.close()
    }
}