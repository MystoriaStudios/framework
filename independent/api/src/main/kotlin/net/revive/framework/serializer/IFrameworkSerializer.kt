package net.revive.framework.serializer

import kotlin.reflect.KClass

interface IFrameworkSerializer {

    fun serialize(obj: Any): String
    fun <T : Any> deserialize(clazz: KClass<T>, input: String): T
}