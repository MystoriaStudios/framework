package net.mystoria.framework.module.loader

import express.ExpressRouter
import net.mystoria.framework.Framework
import net.mystoria.framework.FrameworkApp
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.annotation.container.ContainerPreEnable
import net.mystoria.framework.module.FrameworkModule
import net.mystoria.framework.module.annotation.RestController
import net.mystoria.framework.module.details.FrameworkModuleDetails
import net.mystoria.framework.utils.objectInstance
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URLClassLoader
import java.nio.charset.Charset
import java.util.jar.JarFile
import java.util.logging.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSuperclassOf

class FrameworkModuleLoader(private val directory: File) {

    companion object {
        val loaders: MutableList<URLClassLoader> = mutableListOf()
    }

    init {
        if (!directory.exists()) directory.mkdirs()
    }

    fun loadModule(pluginFile: File) {
        if (!pluginFile.exists() || !pluginFile.isFile) {
            println("Invalid module file: ${pluginFile.name}")
            return
        }

        val pluginClassLoader = URLClassLoader(
            arrayOf(pluginFile.toURI().toURL()),
            this.javaClass.classLoader
        )

        loaders.add(pluginClassLoader)
        println("[Framework] Loaded module ${pluginFile.name} into class loader")
    }

    fun getModuleClass(pluginClassName: String): Class<*>? {
        for (classLoader in loaders) {
            return try {
                classLoader.loadClass(pluginClassName)

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                null
            }
        }
        return null
    }

    fun startup() {
        directory.listFiles()?.forEach { file ->
            Framework.use { framework ->
                framework.log("Framework", file.name)
                runCatching {
                    loadModule(file)

                    val jarFile = JarFile(file)
                    val entry = jarFile.getJarEntry("module.json") ?: throw RuntimeException("Unable to load module from class ${file.name} as there is no module.json present.")

                    val content = IOUtils.toString(jarFile.getInputStream(entry), "UTF-8")
                    val details = framework.serializer.deserialize(FrameworkModuleDetails::class, content)

                    val module = getModuleClass(details.main)?.getDeclaredConstructor()?.newInstance() as FrameworkModule? ?: return@use
                    FrameworkApp.use {
                        it.modules[details.name.lowercase()] = module
                        module.load(details)

                        // Pre enable
                        it.javaClass.declaredMethods.forEach { method ->
                            if (method.isAnnotationPresent(ContainerPreEnable::class.java)) {
                                method.invoke(it)
                            }

                            if (method.isAnnotationPresent(ContainerEnable::class.java)) {
                                method.invoke(it)
                            }
                        }
                    }

                    val entries = jarFile.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.name.endsWith(".class")) {
                            runCatching {
                                val clazz = getModuleClass(entry.name.replace("/", ".").replace(".class", "")) ?: throw RuntimeException("Class not found in loader.")

                                if (clazz.superclass == ExpressRouter::class.java) {
                                    val obj = clazz.kotlin.objectInstance ?: clazz.getDeclaredConstructor().newInstance()
                                    module.routers.add(obj as ExpressRouter)
                                    framework.log(details.name, "Registered router from class ${entry.name}")
                                } else {
                                    framework.log("${details.name} Class Loader", "Found class ${entry.name} in module ${file.name}")
                                }
                            }.onFailure {
                                it.printStackTrace()
                            }
                        }
                    }

                }.onFailure {
                    framework.severe("Framework", "There was an error trying to load that module please fix this.")
                    it.printStackTrace()
                }
            }
        }
    }
}