package net.revive.framework.adapters

import com.google.gson.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.revive.framework.serializer.impl.GsonSerializer
import java.lang.reflect.Type

object ComponentAdapter : JsonSerializer<Component>, JsonDeserializer<Component> {

    val mm = MiniMessage.miniMessage()

    override fun serialize(src: Component?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(mm.serialize(src ?: Component.empty()))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Component {
        return if (json == null) Component.empty()
        else mm.deserialize(json.asString)
    }
}