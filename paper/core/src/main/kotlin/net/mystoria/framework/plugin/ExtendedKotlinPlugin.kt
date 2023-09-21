package net.mystoria.framework.plugin

import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.mystoria.framework.Framework
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorBinder
import net.mystoria.framework.flavor.FlavorOptions
import net.mystoria.framework.flavor.annotation.IgnoreDependencyInjection
import net.mystoria.framework.flavor.annotation.Inject
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

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

    fun supplyFlavor(flavor: Flavor) {
        this.flavor = flavor
    }
    /**
     * END FLAVOUR INJECTION AND HANDLING
     */

    @Inject lateinit var framework: Framework

    override fun load() {
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            logger.info("[Compatability] This version of ${description.name} does not support java ${SystemUtils.JAVA_VERSION}!")
            logger.info("[Compatability] Please use a version higher than 11.")
            Bukkit.shutdown()
            return
        }

        this.flavor = Flavor.create(this::class, FlavorOptions(logger))

        flavor().binders.add(
            FlavorBinder(this@ExtendedKotlinPlugin::class)
        ) to this@ExtendedKotlinPlugin

        flavor {
            bind<ExtendedKotlinPlugin>() to this@ExtendedKotlinPlugin
            bind<ExtendedJavaPlugin>() to this@ExtendedKotlinPlugin
            bind<JavaPlugin>() to this@ExtendedKotlinPlugin

            bind<Server>() to server
            bind<Logger>() to logger
        }
    }
}