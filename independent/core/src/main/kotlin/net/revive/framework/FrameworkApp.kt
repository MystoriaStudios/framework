package net.revive.framework

import express.Express
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.cache.MojangUUIDCacheRouter
import net.revive.framework.module.FrameworkModule
import net.revive.framework.module.loader.FrameworkModuleLoader
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

    fun setup(args: Array<String>) {
        net.revive.framework.Framework.supply(IndependentFramework) {
            val port = Integer.parseInt(System.getProperty("port") ?: "8080")
            express = Express("0.0.0.0")
            express.listen(port)

            express.use(MojangUUIDCacheRouter)

            express.get("/") { req, res ->
                res.send("its online si si si")
            }

            it.log("Framework", "Starting express server on port ${port}.")

            it.log("Framework", "Starting module setup")
            loader = FrameworkModuleLoader(File("modules"))
            it.log("Framework", "Starting module loader")
            loader.startup()
        }

        net.revive.framework.Framework.instance.log("Framework", "Trying to enable ${modules.size} modules.")
        modules.forEach { (key, module) ->
            net.revive.framework.Framework.instance.log("Framework", "Trying to enable $key")
            module.enable()

            module.javaClass.declaredMethods.forEach { method ->
                if (method.isAnnotationPresent(ContainerEnable::class.java)) {
                    method.invoke(module)
                }
            }

            module.routers.forEach { router ->
                net.revive.framework.Framework.instance.log("Framework", "Loaded router from class ${router::class.simpleName}")
                express.use("/${module.details.name.lowercase()}", router)
            }
        }
        net.revive.framework.Framework.instance.log("Framework", "Finished loading modules")

    }
}
