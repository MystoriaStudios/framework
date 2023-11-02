package net.revive.framework

import express.Express
import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.models.*
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.generic.GenericKubernetesApi
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.cache.MojangUUIDCacheRouter
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.grpc.FrameworkGRPCServer
import net.revive.framework.flavor.Flavor
import net.revive.framework.flavor.FlavorOptions
import net.revive.framework.instance.Instance
import net.revive.framework.module.FrameworkNodeModule
import net.revive.framework.module.loader.FrameworkNodeModuleLoader
import net.revive.framework.node.Node
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Thread.sleep
import java.net.Inet4Address
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.findAnnotation

fun main(args: Array<String>) {
    FrameworkApp.setup(args)
}

object FrameworkApp : IConfigProvider {

    override fun getBaseFolder(): File = File("configs")

    fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(this)
    fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(this)

    var state = Node.State.SETUP
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

            //TODO: @Nopox PLEEASE DO THIS
            it.log("Heartbeat", "Beat.")
            val request = Request.Builder()
                .url("https://api.nopox.xyz/api/nodes/${settingsConfig.api_key}/add")
                .post(
                    it.serializer.serialize(
                        Node(
                            settingsConfig.id,
                            "100.110.183.133",
                            settingsConfig.api_key,
                            Node.State.BOOTING,
                            System.currentTimeMillis(),
                            settingsConfig.identifier
                        )
                    ).toRequestBody("text/json".toMediaType())
                )
                .build()

            it.log("Heartbeat", "Trying to beat heart.")
            it.okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseString = response.body?.string()
                    // Handle the successful response here
                    it.log("Heartbeat", "$responseString")
                } else {
                    // Handle the error
                    it.log("Heartbeat Error", "${response.code} - ${response.message}\"")
                }
            }

            sleep(10000)

            it.configure(settingsConfig)
            it.log("Framework", "Loaded settings from config")

            val port = settingsConfig.port
            express = Express("0.0.0.0")
            express.listen(port)

            express.use(MojangUUIDCacheRouter)


            express.get("/") { _, res ->
                res.send("{api: true}")

            }

            express.use { req, res ->
                res.setHeader("Access-Control-Allow-Origin", "*")
            }

            express.post("/setup/:key") { req, res ->
                val key = req.getParam("key")

                state = Node.State.ONLINE

                res.redirect("https://framework-portal.vercel.app/dashboard/nodes/${key}")
            }
            express.get("/peak") { req, res ->
                res.send(it.serializer.serialize(mapOf(
                    "pods" to emptyList<Instance>(),
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

        val heartbeat: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

        Framework.instance.log("Heartbeat", "Starting the node heartbeat.")
        heartbeat.scheduleAtFixedRate({
            Framework.use {
                //TODO: @Nopox PLEEASE DO THIS
                it.log("Heartbeat", "Beat.")
                val request = Request.Builder()
                    .url("https://api.nopox.xyz/api/nodes/${settingsConfig.api_key}/add")
                    .post(
                        it.serializer.serialize(
                            Node(
                                settingsConfig.id,
                                "100.110.183.133",
                                settingsConfig.api_key,
                                state,
                                System.currentTimeMillis(),
                                settingsConfig.identifier
                            )
                        ).toRequestBody("text/json".toMediaType())
                    )
                    .build()

                it.log("Heartbeat", "Trying to beat heart.")
                it.okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseString = response.body?.string()
                        // Handle the successful response here
                        it.log("Heartbeat", "$responseString")
                    } else {
                        // Handle the error
                        it.log("Heartbeat Error", "${response.code} - ${response.message}\"")
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS)

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                Framework.use {
                    it.flavor.close()
                    val request = Request.Builder()
                        .url("https://api.nopox.xyz/api/nodes/${settingsConfig.api_key}/add")
                        .post(
                            it.serializer.serialize(
                                Node(
                                    settingsConfig.id,
                                    "100.110.183.133",
                                    settingsConfig.api_key,
                                    Node.State.OFFLINE,
                                    System.currentTimeMillis(),
                                    settingsConfig.identifier
                                )
                            ).toRequestBody("text/json".toMediaType())
                        )
                        .build()

                    FrameworkGRPCServer.server.shutdownNow()

                    it.log("Heartbeat", "Trying to stop heart.")
                    it.okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val responseString = response.body?.string()
                            // Handle the successful response here
                            it.log("Heartbeat", "$responseString")
                        } else {
                            // Handle the error
                            it.log("Heartbeat Error", "${response.code} - ${response.message}\"")
                        }
                    }
                }
            }
        })
    }
}
