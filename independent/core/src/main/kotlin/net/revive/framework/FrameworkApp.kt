package net.revive.framework

import express.Express
import net.revive.framework.allocation.AllocationRouter
import net.revive.framework.allocation.AllocationService
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.cache.MojangUUIDCacheRouter
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import net.revive.framework.deployment.DeploymentRouter
import net.revive.framework.deployment.DeploymentService
import net.revive.framework.deployment.template.DeploymentTemplate
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorOptions
import net.revive.framework.grpc.FrameworkGRPCServer
import net.revive.framework.heartbeat.HeartbeatService
import net.revive.framework.module.FrameworkNodeModule
import net.revive.framework.module.loader.FrameworkNodeModuleLoader
import net.revive.framework.node.Node
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import kotlin.reflect.full.findAnnotation

fun main(args: Array<String>) {
    FrameworkApp.setup(args)
}

object FrameworkApp : IConfigProvider {

    override fun getBaseFolder(): File = File("configs")

    fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(this)
    fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(this)

    lateinit var loader: FrameworkNodeModuleLoader
    lateinit var express: Express
    lateinit var settingsConfig: FrameworkNodePlatform

    val modules: MutableMap<String, FrameworkNodeModule> = mutableMapOf()

    fun setup(args: Array<String>) {
        println(args)
        if (!getBaseFolder().exists()) getBaseFolder().mkdirs()

        Framework.supply(FrameworkNode) {
            val a = FrameworkNodePlatform::class.findAnnotation<JsonConfig>() ?: throw RuntimeException()
            it.log("Framework", "Trying to load settings config")
            settingsConfig = load<FrameworkNodePlatform>(a)

            it.flavor = Flavor(this::class, FlavorOptions())
            it.flavor.startup()

            AllocationService.mark(settingsConfig.gRPCPort)
            AllocationService.mark(settingsConfig.port)

            HeartbeatService.beat(Node.State.BOOTING)

            sleep(3000)

            it.configure(settingsConfig)
            it.log("Framework", "Loaded settings from config")

            val port = settingsConfig.port
            express = Express(settingsConfig.hostAddress)
            express.listen(port)

            express.use(MojangUUIDCacheRouter)
            express.use(AllocationRouter)
            express.use(DeploymentRouter)


            express.get("/") { _, res ->
                res.send("{api: true}")

            }

            express.use { req, res ->
                res.setHeader("Access-Control-Allow-Origin", "*") // TODO: WHEEN MOVING TO FULL PROD CHASNGE THIS TO https://framework-portal.veercel.app or wtvr we depeloymenet it too i think not suree
            }

            express.post("/setup/:key") { req, res ->
                val key = req.getParam("key")

                settingsConfig.runningState = Node.State.ONLINE
                save(settingsConfig::class.findAnnotation<JsonConfig>()!!, settingsConfig)

                res.redirect("https://framework-portal.vercel.app/dashboard/nodes/${key}")
            }
            express.get("/peak") { req, res ->
                res.send(it.serializer.serialize(mapOf(
                    "pods" to HeartbeatService.podBeats.values,
                    "assignedMemory" to "4096MB",
                    "usedMemory" to Runtime.getRuntime().totalMemory(),
                    "availableCores" to Runtime.getRuntime().availableProcessors()
                )))
            }

            it.log("Framework", "Starting express server on port ${port}.")

            it.log("Framework", "Starting module setup")
            loader = FrameworkNodeModuleLoader(File("modules"))
            it.log("Framework", "Starting module loader")
            loader.startup()
        }

        Framework.instance.log("Framework", "Trying to enable ${modules.size} modules.")
        modules.forEach { (key, module) ->
            Framework.instance.log("Framework", "Trying to enable $key")
            module.enable()

            module.javaClass.declaredMethods.forEach { method ->
                if (method.isAnnotationPresent(ContainerEnable::class.java)) {
                    method.invoke(module)
                }
            }

            module.routers.forEach { router ->
                Framework.instance.log("Framework", "Loaded router from class ${router::class.simpleName}")
                express.use("/${module.details.name.lowercase()}", router)
            }
        }
        Framework.instance.log("Framework", "Finished loading modules")

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                Framework.use {
                    // SSAFELY LCOSES EALL THE EFUCKING DEEPLOYMEENTSS
                    HeartbeatService.beat(Node.State.OFFLINE)
                    it.flavor.close()
                    FrameworkGRPCServer.server.shutdownNow()
                }
            }
        })
    }
}
