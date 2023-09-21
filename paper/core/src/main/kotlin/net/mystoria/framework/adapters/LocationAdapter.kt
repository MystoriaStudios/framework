package net.mystoria.framework.adapters

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

object LocationAdapter : JsonSerializer<Location>, JsonDeserializer<Location> {

    override fun serialize(src: Location?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        if (src == null) return null

        return JsonObject().apply {
            addProperty("world", src.world.name)
            addProperty("x", src.x as Number)
            addProperty("y", src.y as Number)
            addProperty("z", src.z as Number)
            addProperty("yaw", src.yaw as Number)
            addProperty("pitch", src.pitch as Number)
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Location? {
        if (json == null) return null

        with(json.asJsonObject) {
            return Location(
                Bukkit.getWorld(get("world").asString),
                get("x").asDouble,
                get("y").asDouble,
                get("z").asDouble,
                get("yaw").asFloat,
                get("pitch").asFloat
            )
        }
    }

}