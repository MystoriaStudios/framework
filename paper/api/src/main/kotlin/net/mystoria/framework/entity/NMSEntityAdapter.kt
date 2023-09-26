package net.mystoria.framework.entity

import com.google.gson.*
import net.mystoria.framework.flavor.annotation.Inject
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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