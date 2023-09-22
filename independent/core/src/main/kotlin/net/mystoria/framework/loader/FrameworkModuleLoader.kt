package net.mystoria.framework.loader

import net.mystoria.framework.Framework
import net.mystoria.framework.FrameworkApp
import net.mystoria.framework.module.FrameworkModule
import net.mystoria.framework.module.details.FrameworkModuleDetails
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URLClassLoader
import java.nio.charset.Charset
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class FrameworkModuleLoader(private val directory: File) {

    private val loaders: MutableList<URLClassLoader> = mutableListOf()

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

    fun getModuleClass(pluginClassName: String): KClass<*>? {
        for (classLoader in loaders) {
            try {
                return classLoader.loadClass(pluginClassName).kotlin
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun startup() {
        directory.listFiles()?.filter {
            it.extension == ".jar"
        }?.forEach { file ->
            Framework.use { framework ->
                framework.log("Framework", file.name)
                runCatching {
                    loadModule(file)

                    val jarFile = JarFile(file)
                    val entry = jarFile.getJarEntry("module.json") ?: throw RuntimeException("Unable to load module from class ${file.name} as there is no module.json present.")

                    val content = IOUtils.toString(jarFile.getInputStream(entry), "UTF-8")
                    val details = framework.serializer.deserialize(FrameworkModuleDetails::class, content)

                    FrameworkApp.use {
                        val module = getModuleClass(details.main)?.createInstance() as FrameworkModule? ?: return@use
                        it.modules[details.name.lowercase()] = module
                        module.load(details)
                    }
                }.onFailure {
                    framework.severe("Framework", "There was an error trying to load that module please fix this.")
                    it.printStackTrace()
                }
            }
        }
    }
}