package net.revive.framework.serializer.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import net.revive.framework.serializer.IFrameworkSerializer
import kotlin.reflect.KClass

object GsonSerializer : IFrameworkSerializer {

    private val gsonBuilder = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .serializeNulls()

    var gson = gsonBuilder.create()

    fun create(use: GsonBuilder.() -> Unit) {
        synchronized(gsonBuilder) {
            this.gsonBuilder.use()
            this.gson = gsonBuilder.create()
        }
    }

    fun use(use: (Gson) -> Unit) = use.invoke(gson)
    fun useGsonBuilderThenRebuild(
        use: (GsonBuilder) -> Unit
    ) {
        synchronized(gsonBuilder) {
            use.invoke(this.gsonBuilder)
            this.gson = this.gsonBuilder.create()
        }
    }

    override fun serialize(obj: Any) = gson.toJson(obj)
    override fun <T : Any> deserialize(clazz: KClass<T>, input: String) = gson.fromJson(input, clazz.java)
}