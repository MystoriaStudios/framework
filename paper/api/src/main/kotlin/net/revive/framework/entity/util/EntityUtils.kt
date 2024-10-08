package net.revive.framework.entity.util

import org.bukkit.entity.EntityType
import java.util.*

object EntityUtils {
    private var displayNames: MutableMap<EntityType, String> = mutableMapOf()
    private var currentFakeEntityId = 0

    fun getName(type: EntityType): String {
        return displayNames[type] ?: type.name.lowercase().replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun parse(input: String): EntityType? {
        for ((key, value) in displayNames) {
            if (value.replace(" ", "").equals(input, ignoreCase = true)) {
                return key
            }
        }
        for (type in EntityType.entries) {
            if (input.equals(type.toString(), ignoreCase = true)) {
                return type
            }
        }
        return null
    }

    val fakeEntityId: Int get() = currentFakeEntityId--

    init {
        currentFakeEntityId = -1
        displayNames[EntityType.ARROW] = "Arrow"
        displayNames[EntityType.BAT] = "Bat"
        displayNames[EntityType.BLAZE] = "Blaze"
        displayNames[EntityType.BOAT] = "Boat"
        displayNames[EntityType.CAVE_SPIDER] = "Cave Spider"
        displayNames[EntityType.CHICKEN] = "Chicken"
        displayNames[EntityType.COW] = "Cow"
        displayNames[EntityType.CREEPER] = "Creeper"
        displayNames[EntityType.DROPPED_ITEM] = "Item"
        displayNames[EntityType.EGG] = "Egg"
        displayNames[EntityType.ENDER_CRYSTAL] = "Ender Crystal"
        displayNames[EntityType.ENDER_DRAGON] = "Ender Dragon"
        displayNames[EntityType.ENDER_PEARL] = "Ender Pearl"
        displayNames[EntityType.ENDER_SIGNAL] = "Ender Signal"
        displayNames[EntityType.ENDERMAN] = "Enderman"
        displayNames[EntityType.EXPERIENCE_ORB] = "Experience Orb"
        displayNames[EntityType.FALLING_BLOCK] = "Falling Block"
        displayNames[EntityType.FIREBALL] = "Fireball"
        displayNames[EntityType.FIREWORK] = "Firework"
        displayNames[EntityType.FISHING_HOOK] = "Fishing Rod Hook"
        displayNames[EntityType.GHAST] = "Ghast"
        displayNames[EntityType.GIANT] = "Giant"
        displayNames[EntityType.HORSE] = "Horse"
        displayNames[EntityType.IRON_GOLEM] = "Iron Golem"
        displayNames[EntityType.ITEM_FRAME] = "Item Frame"
        displayNames[EntityType.LEASH_HITCH] = "Lead Hitch"
        displayNames[EntityType.LIGHTNING] = "Lightning"
        displayNames[EntityType.MAGMA_CUBE] = "Magma Cube"
        displayNames[EntityType.MINECART] = "Minecart"
        displayNames[EntityType.MINECART_CHEST] = "Chest Minecart"
        displayNames[EntityType.MINECART_FURNACE] = "Furnace Minecart"
        displayNames[EntityType.MINECART_HOPPER] = "Hopper Minecart"
        displayNames[EntityType.MINECART_MOB_SPAWNER] = "Spawner Minecart"
        displayNames[EntityType.MINECART_TNT] = "TNT Minecart"
        displayNames[EntityType.OCELOT] = "Ocelot"
        displayNames[EntityType.PAINTING] = "Painting"
        displayNames[EntityType.PIG] = "Pig"
        displayNames[EntityType.ZOMBIFIED_PIGLIN] = "Zombie Pigman"
        displayNames[EntityType.PLAYER] = "Player"
        displayNames[EntityType.PRIMED_TNT] = "TNT"
        displayNames[EntityType.SHEEP] = "Sheep"
        displayNames[EntityType.SILVERFISH] = "Silverfish"
        displayNames[EntityType.SKELETON] = "Skeleton"
        displayNames[EntityType.SLIME] = "Slime"
        displayNames[EntityType.SMALL_FIREBALL] = "Fireball"
        displayNames[EntityType.SNOWBALL] = "Snowball"
        displayNames[EntityType.SNOWMAN] = "Snowman"
        displayNames[EntityType.SPIDER] = "Spider"
        displayNames[EntityType.SPLASH_POTION] = "Potion"
        displayNames[EntityType.SQUID] = "Squid"
        displayNames[EntityType.THROWN_EXP_BOTTLE] = "Experience Bottle"
        displayNames[EntityType.UNKNOWN] = "Custom"
        displayNames[EntityType.VILLAGER] = "Villager"
        displayNames[EntityType.WITCH] = "Witch"
        displayNames[EntityType.WITHER] = "Wither"
        displayNames[EntityType.WITHER_SKULL] = "Wither Skull"
        displayNames[EntityType.WOLF] = "Wolf"
        displayNames[EntityType.ZOMBIE] = "Zombie"
    }
}
