package net.mystoria.framework.adapters

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.util.*

object WorldAdapter : JsonSerializer<World>, JsonDeserializer<World> {

    override fun serialize(src: World?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        if (src == null) return null

        return JsonObject().apply {
            addProperty("id", src.uid.toString())
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): World? {
        if (json == null) return null

        with(json.asJsonObject) {
            return Bukkit.getWorld(UUID.fromString(get("id").asString))
        }
    }
}