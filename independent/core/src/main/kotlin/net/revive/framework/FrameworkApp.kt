package net.revive.framework

import express.Express
import net.revive.framework.annotation.container.ContainerEnable
import net.revive.framework.cache.MojangUUIDCacheRouter
import net.revive.framework.config.IConfigProvider
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import net.revive.framework.instance.Instance
import net.revive.framework.module.FrameworkModule
import net.revive.framework.module.loader.FrameworkModuleLoader
import java.io.File
import kotlin.reflect.full.findAnnotation

fun main(args: Array<String>) {
    FrameworkApp.setup(args)
}

object FrameworkApp : IConfigProvider {

    override fun getBaseFolder(): File = File("configs")

    fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(this)
    fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(this)

    lateinit var loader: FrameworkModuleLoader
    lateinit var express: Express

    val modules: MutableMap<String, FrameworkModule> = mutableMapOf()

    fun setup(args: Array<String>) {
        println(args)
        if (!getBaseFolder().exists()) getBaseFolder().mkdirs()

        Framework.supply(IndependentFramework) {
            val a = IndependentFrameworkPlatform::class.findAnnotation<JsonConfig>() ?: throw RuntimeException()
            it.log("Framework", "Trying to load settings config")
            val settingsConfig = load<IndependentFrameworkPlatform>(a)

            it.configure(settingsConfig)
            it.log("Framework", "Loaded settings from config")

            object : Thread("Main Thread") {
                /**
                 * Causes this thread to begin execution; the Java Virtual Machine
                 * calls the `run` method of this thread.
                 *
                 *
                 * The result is that two threads are running concurrently: the
                 * current thread (which returns from the call to the
                 * `start` method) and the other thread (which executes its
                 * `run` method).
                 *
                 *
                 * It is never legal to start a thread more than once.
                 * In particular, a thread may not be restarted once it has completed
                 * execution.
                 *
                 * @throws     IllegalThreadStateException  if the thread was already started.
                 * @see .run
                 * @see .stop
                 */
                override fun start() {
                    val port = settingsConfig.port
                    express = Express("0.0.0.0")
                    express.listen(port)

                    express.use(MojangUUIDCacheRouter)

                    express.get("/") { _, res ->
                        res.send("{api: true}")
                    }

                    it.log("Framework", "Starting express server on port ${port}.")
                    super.start()
                }
            }.start()


            it.log("Framework", "Starting module setup")
            loader = FrameworkModuleLoader(File("modules"))
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

        val heartbeat = object : Thread("Node-Backend-Ping-Heart") {
            /**
             * If this thread was constructed using a separate
             * `Runnable` run object, then that
             * `Runnable` object's `run` method is called;
             * otherwise, this method does nothing and returns.
             *
             *
             * Subclasses of `Thread` should override this method.
             *
             * @see .start
             * @see .stop
             * @see .Thread
             */
            override fun run() {
                Framework.useWithReturn {
                    //TODO: @Nopox PLEEASE DO THIS
                    Framework.instance.log("Heartbeat", "Beat.")
                }
                sleep(10000L)
            }
        }
        Framework.instance.log("Heartbeat", "Started beating the heart of the server.")
        heartbeat.start()
    }
}
