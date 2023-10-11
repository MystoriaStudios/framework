package net.revive.framework.metadata

interface IMetaDataHolder {

    val metaData: MutableMap<String, String>

    infix fun has(key: String) = hasMetadata(key)
    fun hasMetadata(key: String) = metaData.containsKey(key)

    operator fun plus(data: Pair<String, String>) = setMetadata(data.first, data.second)
    fun setMetadata(key: String, value: String) {
        metaData[key] = value
    }

    operator fun get(key: String): String? = getMetadata(key)
    fun getMetadata(key: String): String? = metaData[key]

    infix fun delete(key: String) = removeMetadata(key)
    fun removeMetadata(key: String) = metaData.remove(key)
}

inline fun <reified T : Any> IMetaDataHolder.getMetadata(key: String): T? =
    if (hasMetadata(key)) net.revive.framework.Framework.useWithReturn {
        it.serializer.deserialize(T::class, getMetadata(key)!!)
    } else null

inline fun <reified T : Any> IMetaDataHolder.setMetadata(key: String, value: T) = net.revive.framework.Framework.use {
    it.serializer.serialize(value)
}

