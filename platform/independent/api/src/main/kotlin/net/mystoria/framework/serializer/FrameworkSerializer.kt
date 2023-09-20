package net.mystoria.framework.serializer

import kotlin.reflect.KClass

interface FrameworkSerializer {

    fun serialize(obj: Any) : String
    fun <T : Any> deserialize(clazz: KClass<T>, input: String) : T

    fun <T : Any> registerTypeAdapter(adapter: FrameworkTypeAdapter)
}