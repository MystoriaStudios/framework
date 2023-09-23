package net.mystoria.framework.metadata

import net.mystoria.framework.Framework

interface IMetaData {

    val metaData: MutableMap<String, String>

    fun hasMetadata(key: String) = metaData.containsKey(key)
    fun setMetadata(key: String, value: String) {
        metaData[key] = value
    }

    fun getMetadata(key: String) : String? = metaData[key]
}

inline fun <reified T : Any> IMetaData.getMetadata(key: String) : T? = if (hasMetadata(key)) Framework.useWithReturn {
    it.serializer.deserialize(T::class, getMetadata(key)!!)
} else null

inline fun <reified T : Any> IMetaData.setMetadata(key: String, value: T) = Framework.use {
    it.serializer.serialize(value)
}

