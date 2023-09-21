package net.mystoria.framework.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

inline fun ItemBuilder(itemStack: ItemStack, builder: ItemStackBuilder.() -> Unit): ItemStack = ItemStackBuilder(itemStack = itemStack).apply(builder).build()

inline fun ItemBuilder(builder: ItemStackBuilder.() -> Unit): ItemStack = ItemStackBuilder().apply(builder).build()

class ItemStackBuilder(var itemStack: ItemStack = ItemStack(Material.AIR)) {

    fun build(): ItemStack = itemStack

    fun type(material: Material) = apply { itemStack.type = material }

    fun amount(amount: Int) = apply { itemStack.amount = amount }

    fun name(name: Component) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.displayName(name)
        itemStack.itemMeta = meta
    }

    fun lore(lore: List<Component>) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        if (!meta.hasLore()) {
            meta.lore(lore)
        } else {
            meta.lore()!!.addAll(lore)
        }
        itemStack.itemMeta = meta
    }

    fun lore(vararg lore: Component) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        if (!meta.hasLore()) {
            meta.lore(lore.toList())
        } else {
            meta.lore()!!.addAll(lore.toList())
        }
        itemStack.itemMeta = meta
    }

    fun enchantment(enchantment: Enchantment, level: Int) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.addEnchant(enchantment, level, true)
        itemStack.itemMeta = meta
    }

    fun enchantments(enchantments: Map<Enchantment, Int>) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        enchantments.forEach { (enchantment, level) ->
            meta.addEnchant(enchantment, level, true)
        }
        itemStack.itemMeta = meta
    }

    fun flags(itemFlag: ItemFlag) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.addItemFlags(itemFlag)
        itemStack.itemMeta = meta
    }

    fun data(data: Short) = apply {
        this.itemStack.durability = data
    }

    fun model(data: Int) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.setCustomModelData(data)
        itemStack.itemMeta = meta
    }
}