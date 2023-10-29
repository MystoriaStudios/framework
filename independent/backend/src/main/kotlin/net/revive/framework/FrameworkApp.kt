package net.revive.framework

import express.Express
import net.revive.framework.cache.MojangUUIDCacheRouter
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.node.BackendNodeRouter
import java.io.File
import kotlin.reflect.full.findAnnotation

fun main(args: Array<String>) {
    FrameworkApp.setup(args)
}

object FrameworkApp : IConfigProvider {

    override fun getBaseFolder(): File = File("configs")

    fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(this)
    fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(this)

    lateinit var express: Express

    fun setup(args: Array<String>) {
        println(args)
        if (!getBaseFolder().exists()) getBaseFolder().mkdirs()

        Framework.supply(BackendFramework) {
            val a = BackendFrameworkPlatform::class.findAnnotation<JsonConfig>() ?: throw RuntimeException()
            it.log("Framework", "Trying to load settings config")
            val settingsConfig = load<BackendFrameworkPlatform>(a)


            val port = settingsConfig.port
            express = Express("0.0.0.0")
            express.listen(port)

            express.use(MojangUUIDCacheRouter)

            express.use(BackendNodeRouter())

            express.get("/") { _, res ->
                res.send("{api: true}")
            }

            it.log("Framework", "Starting express server on port ${port}.")
            it.log("Framework", "Loaded settings from config")
            it.configure(settingsConfig)
        }
    }
}
