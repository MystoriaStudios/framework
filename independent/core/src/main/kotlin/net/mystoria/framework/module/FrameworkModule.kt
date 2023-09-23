package net.mystoria.framework.module

import com.junglerealms.commons.annotations.custom.CustomAnnotationProcessors
import express.ExpressRouter
import net.mystoria.framework.Framework
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.annotation.container.ContainerPreEnable
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorBinder
import net.mystoria.framework.flavor.FlavorOptions
import net.mystoria.framework.flavor.annotation.IgnoREDependencyInjection
import net.mystoria.framework.flavor.reflections.PackageIndexer
import net.mystoria.framework.module.details.FrameworkModuleDetails
import net.mystoria.framework.utils.objectInstance
import java.util.logging.Level
import java.util.logging.Logger

abstract class FrameworkModule {

    val packageIndexer get() = this.flavor.reflections

    private lateinit var flavor: Flavor
    private val usingFlavor = this::class.java.getAnnotation(IgnoREDependencyInjection::class.java) != null
    val routers = mutableListOf<ExpressRouter>()

    fun flavor() = flavor

    fun flavor(lambda: Flavor.() -> Unit) {
        if (!usingFlavor) return

        flavor.lambda()
    }

    lateinit var details: FrameworkModuleDetails
    lateinit var logger: Logger

    fun load(details: FrameworkModuleDetails) {
        this.details = details

        Framework.use {
            this.logger = it.logger
            it.log("Framework", "Loading module ${details.name} with version ${details.version}.")
        }

        this.flavor = Flavor.create(this::class, FlavorOptions(logger))
        this.flavor.reflections = PackageIndexer(this::class, FlavorOptions(logger))

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
            FlavorBinder(this@FrameworkModule::class)
        ) to this@FrameworkModule

        flavor {
            bind<FrameworkModule>() to this@FrameworkModule

            bind<Logger>() to logger
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

        val processors = CustomAnnotationProcessors.processors

        for (processor in processors) {
            this.packageIndexer.reflections
                .getTypesAnnotatedWith(processor.key.java)
                .map {
                    it.objectInstance() ?: it.newInstance()
                }
                .forEach {
                    this.logger.info(
                        "[Processor] Processing ${
                            it.javaClass.simpleName
                        } annotated with ${
                            processor.key.java.simpleName
                        }."
                    )

                    processor.value.invoke(it)
                }
        }

        this.flavor.startup()
        logger.log(Level.INFO, "Loaded module ${details.name}.")
    }

    fun disable() {

    }
}