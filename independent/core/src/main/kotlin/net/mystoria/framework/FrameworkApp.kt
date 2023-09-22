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
    FrameworkApp.setup(args)
}

object FrameworkApp {

    fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(this)
    fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(this)

    lateinit var loader: FrameworkModuleLoader
    lateinit var express: Express

    val modules: MutableMap<String, FrameworkModule> = mutableMapOf()
    val routers = mutableListOf<ExpressRouter>()

    fun setup(args: Array<String>) {
        val port = Integer.parseInt(System.getProperty("port") ?: "8080")
        express = Express("0.0.0.0")
        express.listen(port)

        Framework.supply(IndependentFramework) {
            it.log("Framework", "Starting express server on port ${port}.")
            it.log("Framework", "Registering annotations")
            CustomAnnotationProcessors.process<RestController> {
                if (it is ExpressRouter) routers.add(it)
            }

            it.log("Framework", "Starting module setup")
            loader = FrameworkModuleLoader(File("modules"))
            it.log("Framework", "Starting module loader")
            loader.startup()
        }

        Framework.instance.log("Framework", "Trying to enable ${modules.size} modules.")
        modules.forEach { (key, module) ->
            Framework.instance.log("Framework", "Trying to enable $key")
            module.enable()
        }
        Framework.instance.log("Framework", "Finished loading modules")

        routers.forEach { router ->
            Framework.instance.log("Framework", "Loaded router from class ${router::class.simpleName}")
            express.use(router)
        }
    }
}
