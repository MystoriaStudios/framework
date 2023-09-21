package net.mystoria.framework.loader

import java.io.File
import java.net.URLClassLoader

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
    }

    fun getModuleClass(pluginClassName: String): Class<*>? {
        for (classLoader in loaders) {
            try {
                return classLoader.loadClass(pluginClassName)
            } catch (e: ClassNotFoundException) {
                // Class not found in this loader, try the next one
            }
        }
        return null
    }
}