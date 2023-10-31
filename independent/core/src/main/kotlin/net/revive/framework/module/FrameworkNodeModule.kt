package net.revive.framework.module

import express.ExpressRouter
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.annotation.custom.CustomAnnotationProcessors
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorBinder
import net.revive.framework.flavor.FlavorOptions
import net.revive.framework.flavor.annotation.IgnoreDependencyInjection
import net.revive.framework.flavor.reflections.PackageIndexer
import net.revive.framework.module.details.FrameworkNodeModuleDetails
import net.revive.framework.utils.objectInstance
import java.util.logging.Level
import java.util.logging.Logger

abstract class FrameworkNodeModule {

    val packageIndexer get() = this.flavor.reflections

    private lateinit var flavor: Flavor
    private val usingFlavor = this::class.java.getAnnotation(IgnoreDependencyInjection::class.java) != null
    val routers = mutableListOf<ExpressRouter>()

    fun flavor() = flavor

    fun flavor(lambda: Flavor.() -> Unit) {
        if (!usingFlavor) return

        flavor.lambda()
    }

    lateinit var details: FrameworkNodeModuleDetails
    lateinit var logger: Logger

    fun load(details: FrameworkNodeModuleDetails) {
        this.details = details

        net.revive.framework.Framework.use {
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
            FlavorBinder(this@FrameworkNodeModule::class)
        ) to this@FrameworkNodeModule

        flavor {
            bind<FrameworkNodeModule>() to this@FrameworkNodeModule

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
                    it.objectInstance() ?: it.getDeclaredConstructor().newInstance()
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