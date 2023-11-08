package net.revive.framework.plugin

import co.aikar.commands.BaseCommand
import co.aikar.commands.BukkitCommandManager
import co.aikar.commands.CommandManager
import co.aikar.commands.PaperCommandManager
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.revive.framework.annotation.Listeners
import net.revive.framework.annotation.Scoreboard
import net.revive.framework.annotation.command.AutoRegister
import net.revive.framework.annotation.command.ManualRegister
import net.revive.framework.annotation.container.ContainerDisable
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.annotation.container.flavor.LazyStartup
import net.revive.framework.annotation.inject.AutoBind
import net.revive.framework.annotation.retrofit.RetrofitService
import net.revive.framework.annotation.retrofit.UsesRetrofit
import net.revive.framework.command.FrameworkCommandManager
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import net.revive.framework.constants.Deployment
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.flavor.FlavorOptions
import net.revive.framework.flavor.annotation.IgnoreDependencyInjection
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.reflections.PackageIndexer
import net.revive.framework.maven.MavenLibraryLoader
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.MenuService.minecraftPlatform
import net.revive.framework.message.FrameworkMessageHandler
import net.revive.framework.plugin.event.KotlinPluginEnableEvent
import net.revive.framework.scoreboard.IScoreboard
import net.revive.framework.scoreboard.ScoreboardService
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.sender.FrameworkSender
import net.revive.framework.sender.PaperFrameworkPlayer
import net.revive.framework.sentry.SentryService
import net.revive.framework.serializer.IFrameworkSerializer
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.utils.Strings
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.objectInstance
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation

/**
 * An extension of [ExtendedJavaPlugin] with
 * support for our custom annotation-based
 * plugin framework.
 */
@Suppress("DEPRECATION")
open class ExtendedKotlinPlugin : ExtendedJavaPlugin(), IConfigProvider {

    override fun getBaseFolder() = dataFolder

    /**
     * START FLAVOR INJECTION AND HANDLING
     */
    lateinit var packageIndexer: PackageIndexer
    private lateinit var flavor: Flavor
    var trackedConfigs = mutableMapOf<JsonConfig, Any>()

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
        MavenLibraryLoader.loadAll(this::class.java)
        this.flavor = Flavor.create((Bukkit.getPluginManager().getPlugin(description.name) ?: this)::class, FlavorOptions(logger))
        this.flavor.reflections = PackageIndexer(this::class, FlavorOptions(logger), listOf(this.classLoader))
        this.packageIndexer = this.flavor.reflections

        if (this::class.hasAnnotation<UsesRetrofit>()) {
            retrofit = Retrofit.Builder()
                .baseUrl("${Deployment.Security.API_BASE_URL}/${this.description.name}/")
                .client(net.revive.framework.Framework.useWithReturn {
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

        this::class.declaredFunctions
            .filter {
                it.hasAnnotation<ContainerPreEnable>()
            }.forEach {
                kotlin.runCatching {
                    it.call()
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
        net.revive.framework.Framework.use {
            flavor {
                bind<SentryService>() to it.sentryService
                bind<FrameworkMessageHandler>() to it.messageHandler
                bind<IFrameworkSerializer>() to it.serializer
            }
        }

        this.commandManager = FrameworkCommandManager(this)

        flavor {
            bind<FrameworkCommandManager>() to commandManager
            bind<CommandManager<*, *, *, *, *, *>>() to commandManager
            bind<BukkitCommandManager>() to commandManager
            bind<PaperCommandManager>() to commandManager
        }

        commandManager
            .commandContexts
            .registerIssuerAwareContext(FrameworkSender::class.java) {
                return@registerIssuerAwareContext minecraftPlatform
                    .getPlayer(it.issuer.uniqueId)
            }

        commandManager
            .commandContexts
            .registerIssuerAwareContext(PaperFrameworkPlayer::class.java) {
                if (!it.issuer.isPlayer) return@registerIssuerAwareContext null

                return@registerIssuerAwareContext minecraftPlatform
                    .getPlayer(it.issuer.uniqueId) as PaperFrameworkPlayer
            }

        commandManager
            .commandContexts
            .registerIssuerAwareContext(AbstractFrameworkPlayer::class.java) {
                if (!it.issuer.isPlayer) return@registerIssuerAwareContext null

                return@registerIssuerAwareContext minecraftPlatform
                    .getPlayer(it.issuer.uniqueId)
            }


        var commands = 0
        this.packageIndexer
            .getTypesAnnotatedWith<AutoRegister>()
            .forEach {
                kotlin.runCatching {
                    val instance = it.objectInstance() ?: it.getDeclaredConstructor().newInstance()

                    this.flavor.inject(instance)

                    this.commandManager.registerCommand(instance as BaseCommand)
                    commands++
                }.onFailure {
                    logger.log(Level.WARNING, "Failed to register command", it)
                }
            }
        net.revive.framework.Framework.use {
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

        this.packageIndexer
            .getSubTypes<IMenu>()
            .mapNotNull {
                it.kotlin.objectInstance
            }
            .forEach {
                this.flavor.inject(it)
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

        this.packageIndexer
            .getTypesAnnotatedWith<Scoreboard>()
            .mapNotNull {
                it.kotlin.objectInstance
            }.filterIsInstance<IScoreboard>()
            .forEach {
                this.flavor.inject(it)

                ScoreboardService.updatePrimaryProvider(it)
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

        trackedConfigs.filter {
            it.key.autoSave
        }.forEach(this::save)

        this.commandManager.unregisterCommands()
        this.flavor.close()
    }
}