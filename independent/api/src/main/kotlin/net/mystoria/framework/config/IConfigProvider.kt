package net.mystoria.framework.config

import net.mystoria.framework.Framework
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.reflect.KClass

interface IConfigProvider {
    fun getBaseFolder() : File
}

inline fun <reified T : Any> IConfigProvider.load(config: JsonConfig) : T {
    val file = File(getBaseFolder(), config.fileName)

    return if (!file.exists()) {
        val inst = T::class.java.getConstructor().newInstance()
        save<T>(config, inst)
    } else {
        Framework.useWithReturn {
            it.serializer.deserialize(T::class, FileUtils.readFileToString(file, "UTF-8"))
        }
    }
}

fun IConfigProvider.load(config: JsonConfig, clazz: KClass<*>) : Any {
    val file = File(getBaseFolder(), config.fileName)

    return if (!file.exists()) {
        val inst = clazz::class.java.getConstructor().newInstance()
        save(config, inst)
    } else {
        Framework.useWithReturn {
            it.serializer.deserialize(clazz::class, FileUtils.readFileToString(file, "UTF-8"))
        }
    }
}

inline fun <reified T : Any> IConfigProvider.save(config: JsonConfig, inst: T) : T {
    if (!getBaseFolder().exists()) getBaseFolder().mkdirs()
    val file = File(getBaseFolder(), config.fileName)

    if (!file.exists()) file.createNewFile()

    FileUtils.writeStringToFile(file, Framework.useWithReturn {
        it.serializer.serialize(inst)
    }, "UTF-8")

    return inst
}