package net.mystoria.framework

import com.junglerealms.commons.annotations.custom.CustomAnnotationProcessors
import express.Express
import express.ExpressRouter
import net.mystoria.framework.annotation.RestController
import net.mystoria.framework.flavor.Flavor
import net.mystoria.framework.flavor.FlavorOptions
import net.mystoria.framework.loader.FrameworkModuleLoader
import net.mystoria.framework.module.FrameworkModule
import net.mystoria.framework.updater.UpdaterIndependentPlatform
import net.mystoria.framework.updater.UpdaterService
import java.io.File

fun main(args: Array<String>) {
    FrameworkApp().setup(args)
}

class FrameworkApp {

    companion object {
        lateinit var instance: FrameworkApp

        fun supply(framework: FrameworkApp) {
            instance = framework
        }

        fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(instance)
        fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(instance)
    }

    lateinit var loader: FrameworkModuleLoader
    lateinit var express: Express

    val modules: MutableMap<String, FrameworkModule> = mutableMapOf()
    val routers = mutableListOf<ExpressRouter>()

    fun setup(args: Array<String>) {
        supply(this)
        Framework.supply(IndependentFramework) {
            it.log("Framework", "Registering annotations")
            CustomAnnotationProcessors.process<RestController> {
                if (it is ExpressRouter) routers.add(it)
            }
            runCatching {
                it.log("Framework", "Configuring updater platform")
                UpdaterService.configure(UpdaterIndependentPlatform)
            }

            it.log("Framework", "Starting module setup")
            loader = FrameworkModuleLoader(File("modules"))
            it.log("Framework", "Starting module loader")
            loader.startup()
            it.log("Framework", "Starting framework flavor instance")
            it.flavor = Flavor(this::class, FlavorOptions())
            it.flavor.startup()

            it.log("Framework", "Trying to enable ${modules.size} modules.")
            modules.forEach { (key, module) ->
                it.log("Framework", "Trying to enable $key")
                module.enable()
            }
            it.log("Framework", "Finished loading modules")

            val port = Integer.parseInt(System.getProperty("port") ?: "8080")
            express = Express("0.0.0.0")
            it.log("Framework", "Starting framework server on port ${port}.")

            routers.forEach { router ->
                express.use(router)
            }

            express.listen(port)
        }

/*        Runtime.getRuntime().addShutdownHook(Thread {
            modules.forEach {
                it.value.disable()
            }
        })*/
    }
}