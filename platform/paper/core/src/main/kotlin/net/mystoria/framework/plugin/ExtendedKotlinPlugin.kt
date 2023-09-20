package net.mystoria.framework.plugin

import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.annotation.IgnoreDependencyInjection

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
     *
     */
}