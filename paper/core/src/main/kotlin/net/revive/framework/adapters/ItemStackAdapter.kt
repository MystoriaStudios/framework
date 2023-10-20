package net.revive.framework.adapters

import com.google.gson.*
import me.lucko.helper.serialize.InventorySerialization
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

object ItemStackAdapter : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    override fun serialize(src: ItemStack, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("item", InventorySerialization.encodeItemStackToString(src))
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): ItemStack? {
        if (json == null) return null

        return InventorySerialization.decodeItemStack(json.asJsonObject["item"].asString)
    }
}