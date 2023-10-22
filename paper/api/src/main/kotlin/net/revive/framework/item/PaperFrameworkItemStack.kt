package net.revive.framework.item

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.material.Colorable

class PaperFrameworkItemStack(itemStack: ItemStack) : AbstractFrameworkItemStack<ItemStack>(itemStack) {
    override fun setType(material: Key) {
        item.type = Material.matchMaterial(material.toString())!!
    }

    override fun setAmount(amount: Int) {
        item.amount = amount
    }

    override fun setName(name: Component) {
        item.editMeta {
            it.displayName(name)
        }
    }

    override fun addLore(newLore: List<Component>) {
        val meta = item.itemMeta ?: Bukkit.getItemFactory().getItemMeta(item.type);
        if (!meta.hasLore()) {
            meta.lore(newLore)
        } else {
            meta.lore()!!.addAll(newLore)
        }
        item.itemMeta = meta
    }

    override fun setLore(newLore: List<Component>) {
        val meta = item.itemMeta ?: Bukkit.getItemFactory().getItemMeta(item.type);
        meta.lore(newLore)
        item.itemMeta = meta
    }

    override fun setDurability(durability: Int) {
        val meta = item.itemMeta ?: Bukkit.getItemFactory().getItemMeta(item.type);
        if (meta is Damageable) {
            meta.damage = durability
            item.itemMeta = meta
        } else throw RuntimeException()
    }

    override fun setCustomModelData(modelData: Int) {
        item.editMeta {
            it.setCustomModelData(modelData)
        }
    }

    override fun addFlag(flag: FrameworkItemFlag) {
        item.addItemFlags(toItemFlag(flag))
    }

    override fun setColor(color: TextColor) {
        item.editMeta {
            if (it is Colorable) {
                it.color = DyeColor.getByColor(Color.fromRGB(color.value()))
            }
        }
    }

    private fun toItemFlag(flag: FrameworkItemFlag): ItemFlag {
        return when(flag) {
            FrameworkItemFlag.HIDE_ARMOR_TRIM -> ItemFlag.HIDE_ARMOR_TRIM
            FrameworkItemFlag.HIDE_ATTRIBUTES -> ItemFlag.HIDE_ATTRIBUTES
            FrameworkItemFlag.HIDE_DESTROYS -> ItemFlag.HIDE_DESTROYS
            FrameworkItemFlag.HIDE_DYE -> ItemFlag.HIDE_DYE
            FrameworkItemFlag.HIDE_ENCHANTS -> ItemFlag.HIDE_ENCHANTS
            FrameworkItemFlag.HIDE_ITEM_SPECIFICS -> ItemFlag.HIDE_ITEM_SPECIFICS
            FrameworkItemFlag.HIDE_PLACED_ON -> ItemFlag.HIDE_PLACED_ON
            FrameworkItemFlag.HIDE_UNBREAKABLE -> ItemFlag.HIDE_UNBREAKABLE
            else -> {
                throw UnsupportedOperationException("You prolly forgot some shit retard: ${flag.name}")
            }
        }
    }
}