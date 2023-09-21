package net.mystoria.framework.adapters

import com.google.gson.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

object ItemStackAdapter : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    override fun serialize(src: ItemStack?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        if (src == null) return null

        val output = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(output)
        dataOutput.writeObject(src)
        dataOutput.close()

        return JsonObject().apply {
            addProperty("item", Base64Coder.encodeLines(output.toByteArray()))
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ItemStack? {
        if (json == null) return null

        with(json.asJsonObject) {
            val input = ByteArrayInputStream(Base64Coder.decodeLines(get("item").asString))
            val dataInput = BukkitObjectInputStream(input)

            return dataInput.readObject() as ItemStack
        }
    }
}