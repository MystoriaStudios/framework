package net.revive.framework.module.loader

import express.ExpressRouter
import net.revive.framework.FrameworkApp
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.module.FrameworkModule
import net.revive.framework.module.details.FrameworkModuleDetails
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

class FrameworkModuleLoader(private val directory: File) {

    companion object {
        val loaders: MutableMap<String, URLClassLoader> = mutableMapOf()
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

        loaders[pluginFile.name] = pluginClassLoader
        println("[Framework] Loaded module ${pluginFile.name} into class loader")
    }

    fun getModuleClass(file: File, pluginClassName: String): Class<*>? {
        return try {
            loaders[file.name]?.loadClass(pluginClassName)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    fun startup() {
        directory.listFiles()?.forEach { file ->
            net.revive.framework.Framework.use { framework ->
                framework.log("Framework", file.name)
                runCatching {
                    loadModule(file)

                    val jarFile = JarFile(file)
                    val entry = jarFile.getJarEntry("module.json")
                        ?: throw RuntimeException("Unable to load module from class ${file.name} as there is no module.json present.")

                    val content = IOUtils.toString(jarFile.getInputStream(entry), "UTF-8")
                    val details = framework.serializer.deserialize(FrameworkModuleDetails::class, content)

                    val module =
                        getModuleClass(file, details.main)?.getDeclaredConstructor()?.newInstance() as FrameworkModule?
                            ?: return@use
                    FrameworkApp.use {
                        it.modules[details.name.lowercase()] = module
                        module.load(details)

                        // Pre enable
                        it.javaClass.declaredMethods.forEach { method ->
                            if (method.isAnnotationPresent(ContainerPreEnable::class.java)) {
                                method.invoke(it)
                            }
                        }
                    }

                    val entries = jarFile.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.name.endsWith(".class")) {
                            runCatching {
                                val clazz = getModuleClass(file, entry.name.replace("/", ".").replace(".class", ""))
                                    ?: throw RuntimeException("Class not found in loader.")

                                if (clazz.superclass == ExpressRouter::class.java) {
                                    val obj =
                                        clazz.kotlin.objectInstance ?: clazz.getDeclaredConstructor().newInstance()
                                    module.routers.add(obj as ExpressRouter)
                                    framework.log(details.name, "Registered router from class ${entry.name}")
                                }
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