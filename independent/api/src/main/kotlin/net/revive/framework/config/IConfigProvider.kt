package net.revive.framework.config

import net.revive.framework.Framework
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.KClass

interface IConfigProvider {
    fun getBaseFolder(): File
}

inline fun <reified T : Any> IConfigProvider.load(config: JsonConfig): T {
    val file = File(getBaseFolder(), config.fileName)

    return if (!file.exists()) {
        val inst = T::class.java.getConstructor().newInstance()
        save<T>(config, inst)
    } else {
        Framework.useWithReturn {
            it.serializer.deserialize(T::class, file.readText())
        }
    }
}

fun IConfigProvider.load(config: JsonConfig, clazz: KClass<*>): Any {
    val file = File(getBaseFolder(), config.fileName)

    return if (!file.exists()) {
        throw FileNotFoundException()
    } else {
        Framework.useWithReturn {
            it.serializer.deserialize(clazz, file.readText())
        }
    }
}

inline fun <reified T : Any> IConfigProvider.save(config: JsonConfig, inst: T): T {
    if (!getBaseFolder().exists()) getBaseFolder().mkdirs()
    val file = File(getBaseFolder(), config.fileName)

    if (!file.exists()) file.createNewFile()

    file.writeText(Framework.useWithReturn {
        it.serializer.serialize(inst)
    })

    return inst
}