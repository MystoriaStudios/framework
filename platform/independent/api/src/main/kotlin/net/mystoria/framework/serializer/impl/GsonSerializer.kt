package net.mystoria.framework.serializer.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import net.mystoria.framework.serializer.FrameworkSerializer
import net.mystoria.framework.serializer.FrameworkTypeAdapter
import kotlin.reflect.KClass

object GsonSerializer : FrameworkSerializer {

    private val gsonBuilder = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls()

    private var gson = gsonBuilder.create()

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

    override fun serialize(obj: Any): String {
        TODO("Not yet implemented")
    }

    override fun <T : Any> deserialize(clazz: KClass<T>, input: String): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> registerTypeAdapter(adapter: FrameworkTypeAdapter) {
        useGsonBuilderThenRebuild {
            it.registerTypeAdapter(adapter.clazz, adapter)
        }
    }
}