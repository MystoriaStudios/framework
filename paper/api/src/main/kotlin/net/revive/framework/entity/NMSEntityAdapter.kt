package net.revive.framework.entity

import com.google.gson.*
import net.revive.framework.flavor.annotation.Inject
import java.lang.reflect.Type

object NMSEntityAdapter : JsonSerializer<AbstractNMSEntity>, JsonDeserializer<AbstractNMSEntity> {

    @Inject
    lateinit var entityHandler: IEntityHandler

    override fun serialize(src: AbstractNMSEntity, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val json = JsonObject()
        json.addProperty("type", src::class.java.name)
        json.add("properties", context!!.serialize(src, src::class.java))
        return json
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): AbstractNMSEntity? {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        val properties = jsonObject.get("properties")

        try {
            if (context != null) {
                return context.deserialize<AbstractNMSEntity>(properties, Class.forName(type)).also {
                    // TODO: Resolve this
                    //it.initializeData()
                    entityHandler.trackEntity(it)
                }
            }
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown type: $type", e)
        }

        return null
    }
}