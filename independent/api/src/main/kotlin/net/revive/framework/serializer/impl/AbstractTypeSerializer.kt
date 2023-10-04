package net.revive.framework.serializer.impl

import com.google.gson.*
import net.revive.framework.serializer.IAbstractTypeSerializable
import java.lang.reflect.Type

class AbstractTypeSerializer<T : IAbstractTypeSerializable> : JsonSerializer<T>, JsonDeserializer<T> {

    override fun serialize(src: T, typeOf: Type, context: JsonSerializationContext): JsonElement {
        val abstractType = src.getAbstractType() as Class<*>
        val json = JsonObject()
        json.addProperty("type", abstractType.name)
        json.add("properties", context.serialize(src, src.getAbstractType()))
        return json
    }

    override fun deserialize(json: JsonElement, typeOf: Type, context: JsonDeserializationContext): T {
        val type = json.asJsonObject.get("type").asString
        val properties = json.asJsonObject.get("properties")

        try {
            return context.deserialize(properties, Class.forName(type))
        } catch (e: ClassNotFoundException) {
            throw JsonParseException("Unknown type: $type", e)
        }
    }

}