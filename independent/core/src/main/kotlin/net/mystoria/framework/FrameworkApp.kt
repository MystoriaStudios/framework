package net.mystoria.framework

import com.junglerealms.commons.annotations.custom.CustomAnnotationProcessors
import io.jooby.annotation.GET
import io.jooby.annotation.POST
import io.jooby.kt.Kooby
import net.mystoria.framework.annotation.RestController
import net.mystoria.framework.loader.FrameworkModuleLoader
import net.mystoria.framework.module.FrameworkModule
import java.io.File
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

fun main(args: Array<String>) {
    FrameworkApp().main(args)
}

class FrameworkApp : Kooby() {

    companion object {
        lateinit var instance: FrameworkApp

        fun supply(framework: FrameworkApp) {
            instance = framework
        }

        fun use(lambda: (FrameworkApp) -> Unit) = lambda.invoke(instance)
        fun <T> useWithReturn(lambda: (FrameworkApp) -> T) = lambda.invoke(instance)
    }

    lateinit var loader: FrameworkModuleLoader

    val modules: MutableMap<String, FrameworkModule> = mutableMapOf()

    fun main(args: Array<String>) {
        supply(this)

        CustomAnnotationProcessors.process<RestController> {
            it::class.functions.forEach { function ->
                if (function.hasAnnotation<GET>()) {
                    val annotation = function.findAnnotation<GET>() ?: throw RuntimeException()

                    annotation.path.forEach { path ->
                        get(path) {
                            function.call(this.ctx) ?: "{response: \"The framework server returned no value.\", code: 500}"
                        }
                    }
                }

                if (function.hasAnnotation<POST>()) {
                    val annotation = function.findAnnotation<POST>() ?: throw RuntimeException()

                    annotation.path.forEach { path ->
                        post(path) {
                            function.call(this.ctx) ?: "{response: \"The framework server returned no value.\", code: 500}"
                        }
                    }
                }
            }
        }

        loader = FrameworkModuleLoader(File("modules"))
        Framework.supply(IndependentFramework) {
            loader.startup()

            modules.forEach { (key, module) ->
                module.enable()
            }
        }



        Runtime.getRuntime().addShutdownHook(Thread {
            modules.forEach {
                it.value.disable()
            }
        })
    }
}