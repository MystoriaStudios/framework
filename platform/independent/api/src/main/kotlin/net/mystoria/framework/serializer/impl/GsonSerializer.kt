package net.mystoria.framework.serializer.impl

import net.mystoria.framework.serializer.FrameworkSerializer
import kotlin.reflect.KClass

object GsonSerializer : FrameworkSerializer {

    private val gson = Gson

    override fun serialize(obj: Any): String {
        TODO("Not yet implemented")
    }

    override fun <T : Any> deserialize(clazz: KClass<T>, input: String): T {
        TODO("Not yet implemented")
    }
}