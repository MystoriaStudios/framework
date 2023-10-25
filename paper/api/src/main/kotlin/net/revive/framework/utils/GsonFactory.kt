package net.revive.framework.utils

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import net.revive.framework.serializer.impl.GsonSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.IOException
import java.util.*

/* *
* Created by Joshua Bell (RingOfStorms)
*
* Post explaining here: [URL]http://bukkit.org/threads/gsonfactory-gson-that-works-on-itemstack-potioneffect-location-objects.331161/[/URL]
* */
object GsonFactory {
    /*
    - I want to not use Bukkit parsing for most objects... it's kind of clunky
    - Instead... I want to start using any of Mojang's tags
    - They're really well documented + built into MC, and handled by them.
    - Rather than kill your old code, I'm going to write TypeAdapaters using Mojang's stuff.
     */
    private val g = Gson()
    private const val CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY"

    // START FRAMEWORK
    fun applyPlatformChanges() {
        GsonSerializer.gsonBuilder.addSerializationExclusionStrategy(ExposeExlusion())
                .addDeserializationExclusionStrategy(ExposeExlusion())
                .registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackGsonAdapter())
                .registerTypeAdapter(PotionEffect::class.java, PotionEffectGsonAdapter())
                .registerTypeAdapter(Location::class.java, LocationGsonAdapter())
                .registerTypeAdapter(Date::class.java, DateGsonAdapter())
                .disableHtmlEscaping()
    }
    // END FRAMEWORK

    private fun recursiveSerialization(o: ConfigurationSerializable): MutableMap<String, Any> {
        val originalMap = o.serialize()
        val map: MutableMap<String, Any> = HashMap()
        for ((key, o2) in originalMap) {
            if (o2 is ConfigurationSerializable) {
                val serializable = o2
                val newMap = recursiveSerialization(serializable)
                newMap[CLASS_KEY] = ConfigurationSerialization.getAlias(serializable.javaClass)
                map[key] = newMap
            }
        }
        map[CLASS_KEY] = ConfigurationSerialization.getAlias(o.javaClass)
        return map
    }

    private fun recursiveDoubleToInteger(originalMap: Map<String, Any?>?): MutableMap<String, Any?> {
        val map: MutableMap<String, Any?> = HashMap()
        for ((key, o) in originalMap!!) {
            if (o is Double) {
                val i = o.toInt()
                map[key] = i
            } else if (o is Map<*, *>) {
                val subMap = o as Map<String, Any?>
                map[key] = recursiveDoubleToInteger(subMap)
            } else {
                map[key] = o
            }
        }
        return map
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FIELD)
    annotation class Ignore
    private class ExposeExlusion : ExclusionStrategy {
        override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
            val ignore = fieldAttributes.getAnnotation(Ignore::class.java)
            if (ignore != null) return true
            val expose = fieldAttributes.getAnnotation(
                Expose::class.java
            )
            return expose != null && (!expose.serialize || !expose.deserialize)
        }

        override fun shouldSkipClass(aClass: Class<*>?): Boolean {
            return false
        }
    }

    private class ItemStackGsonAdapter : TypeAdapter<ItemStack?>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, itemStack: ItemStack?) {
            if (itemStack == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.value(getRaw(itemStack))
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): ItemStack? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            return fromRaw(jsonReader.nextString())
        }

        private fun getRaw(item: ItemStack?): String? {
            if (item == null) {
                return null
            }
            val serial = item.serialize()
            if (serial.containsKey("damage")) {
                if (item.type.maxDurability > 0) {
                    serial["max-damage"] = item.type.maxDurability
                }
            }
            if (!item.itemMeta.hasDisplayName()) {
                serial["displayname"] = item.displayName()
            }
            serial["type"] = item.type.name
            if (serial["meta"] != null) {
                val itemMeta = item.itemMeta
                val originalMeta = itemMeta.serialize()
                val meta: MutableMap<String, Any> = HashMap()
                for ((key, value) in originalMeta) meta[key] = value
                var o: Any
                for ((key, value) in meta) {
                    o = value
                    if (o is ConfigurationSerializable) {
                        val serialized: Map<String, Any> = recursiveSerialization(
                            o
                        )
                        meta[key] = serialized
                    }
                }
                serial["meta"] = meta
            }
            return g.toJson(serial)
        }

        private fun fromRaw(raw: String): ItemStack? {
            val keys = g.fromJson<MutableMap<String, Any?>>(raw, seriType)
            if (keys["amount"] != null) {
                val d = keys["amount"] as Double?
                val i = d!!.toInt()
                keys["amount"] = i
            }
            val item: ItemStack
            item = try {
                ItemStack.deserialize(keys)
            } catch (e: Exception) {
                return null
            }
            if (item == null) return null
            if (keys.containsKey("meta")) {
                var itemmeta = keys["meta"] as MutableMap<String, Any?>?
                itemmeta!!.remove("max-damage")
                if (itemmeta.containsKey("displayname") && itemmeta["displayname"] == item.type.toString()) {
                    itemmeta.remove("displayname")
                }
                itemmeta.remove("id")
                itemmeta = recursiveDoubleToInteger(itemmeta)
                val meta = ConfigurationSerialization.deserializeObject(
                    itemmeta,
                    ConfigurationSerialization.getClassByAlias("ItemMeta")!!
                ) as ItemMeta?
                item.setItemMeta(meta)
            }
            return item
        }

        companion object {
            private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
        }
    }

    private class PotionEffectGsonAdapter : TypeAdapter<PotionEffect?>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, potionEffect: PotionEffect?) {
            if (potionEffect == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.value(getRaw(potionEffect))
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): PotionEffect? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            return fromRaw(jsonReader.nextString())
        }

        private fun getRaw(potion: PotionEffect): String {
            val serial = potion.serialize()
            return g.toJson(serial)
        }

        private fun fromRaw(raw: String): PotionEffect {
            val keys = g.fromJson<Map<String, Any>>(raw, seriType)
            return PotionEffect(
                PotionEffectType.getById((keys[TYPE] as Double?)!!.toInt())!!,
                (keys[DURATION] as Double?)!!.toInt(),
                (keys[AMPLIFIER] as Double?)!!.toInt(),
                (keys[AMBIENT] as Boolean?)!!
            )
        }

        companion object {
            private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
            private const val TYPE = "effect"
            private const val DURATION = "duration"
            private const val AMPLIFIER = "amplifier"
            private const val AMBIENT = "ambient"
        }
    }

    private class LocationGsonAdapter : TypeAdapter<Location?>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, location: Location?) {
            if (location == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.value(getRaw(location))
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Location? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            return fromRaw(jsonReader.nextString())
        }

        private fun getRaw(location: Location): String {
            val serial: MutableMap<String, Any> = HashMap()
            serial[UUID] = location.world.uid.toString()
            serial[X] = java.lang.Double.toString(location.x)
            serial[Y] = java.lang.Double.toString(location.y)
            serial[Z] = java.lang.Double.toString(location.z)
            serial[YAW] = java.lang.Float.toString(location.yaw)
            serial[PITCH] = java.lang.Float.toString(location.pitch)
            return g.toJson(serial)
        }

        private fun fromRaw(raw: String): Location {
            val keys = g.fromJson<Map<String, Any>>(raw, seriType)
            val w = Bukkit.getWorld(java.util.UUID.fromString(keys[UUID] as String?))
            return Location(
                w,
                (keys[X] as String?)!!.toDouble(),
                (keys[Y] as String?)!!.toDouble(),
                (keys[Z] as String?)!!.toDouble(),
                (keys[YAW] as String?)!!.toFloat(),
                (keys[PITCH] as String?)!!.toFloat()
            )
        }

        companion object {
            private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
            private const val UUID = "uuid"
            private const val X = "x"
            private const val Y = "y"
            private const val Z = "z"
            private const val YAW = "yaw"
            private const val PITCH = "pitch"
        }
    }

    private class DateGsonAdapter : TypeAdapter<Date?>() {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, date: Date?) {
            if (date == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.value(date.time)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Date? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            return Date(jsonReader.nextLong())
        }
    }
}
