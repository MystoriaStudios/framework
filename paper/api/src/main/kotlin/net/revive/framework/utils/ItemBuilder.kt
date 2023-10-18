package net.revive.framework.utils

import com.google.common.base.Preconditions
import net.kyori.adventure.text.Component
import net.revive.framework.component.IFrameworkComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

inline fun itemBuilder(itemStack: ItemStack, builder: ItemStackBuilder.() -> Unit): ItemStack =
    ItemStackBuilder(itemStack = itemStack).apply(builder).build()

inline fun itemBuilder(builder: ItemStackBuilder.() -> Unit): ItemStack = ItemStackBuilder().apply(builder).build()

class ItemStackBuilder(var itemStack: ItemStack = ItemStack(Material.AIR)) {

    fun build(): ItemStack = itemStack

    fun type(material: Material) = apply { itemStack.type = material }

    fun amount(amount: Int) = apply { itemStack.amount = amount }

    fun name(name: Component) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.displayName(name)
        itemStack.itemMeta = meta
    }

    @JvmName("fLore")
    fun lore(lore: List<IFrameworkComponent>) = apply {
        lore(lore.map(IFrameworkComponent::build))
    }

    @JvmName("fLore")
    fun lore(vararg lore: IFrameworkComponent) = apply {
        lore(lore.map(IFrameworkComponent::build))
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

    fun durability(data: Int) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        if (meta is Damageable) {
            meta.damage = data
            itemStack.itemMeta = meta
        } else throw RuntimeException()
    }

    fun model(data: Int) = apply {
        val meta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type);
        meta.setCustomModelData(data)
        itemStack.itemMeta = meta
    }

    fun color(color: org.bukkit.Color?) = apply {
        val meta = itemStack.itemMeta as? LeatherArmorMeta
            ?: throw UnsupportedOperationException("Cannot set color of a non-leather armor item.")
        meta.setColor(color)
        itemStack.itemMeta = meta
        return this
    }
}

class ItemBuilder {
    private val item: ItemStack

    private constructor(material: Material, amount: Int) {
        Preconditions.checkArgument(amount > 0, "Amount cannot be lower than 0.")
        item = ItemStack(material, amount)
    }

    private constructor(item: ItemStack) {
        this.item = item
    }

    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun data(data: Int): ItemBuilder {
        val meta = item.itemMeta
        if (meta is Damageable) {
            meta.damage = data
            item.itemMeta = meta
            return this
        } else throw RuntimeException()
    }

    fun flag(flag: ItemFlag): ItemBuilder {
        val meta = item.itemMeta
        meta.addItemFlags(flag)
        item.itemMeta = meta
        return this
    }

    fun enchant(enchantment: Enchantment?, level: Int): ItemBuilder {
        item.addUnsafeEnchantment(enchantment!!, level)
        return this
    }

    fun unenchant(enchantment: Enchantment?): ItemBuilder {
        item.removeEnchantment(enchantment!!)
        return this
    }

    fun name(displayName: Component?): ItemBuilder {
        val meta = item.itemMeta
        meta.displayName(displayName)
        item.itemMeta = meta
        return this
    }

    fun owner(name: String?): ItemBuilder {
        val meta = item.itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(name!!)
        item.itemMeta = meta
        return this
    }

    fun lore(l: Collection<Component>): ItemBuilder {
        val meta = item.itemMeta
        meta.lore(l.toMutableList())
        item.setItemMeta(meta)
        return this
    }

    fun color(color: org.bukkit.Color?): ItemBuilder {
        val meta = item.itemMeta as? LeatherArmorMeta
            ?: throw UnsupportedOperationException("Cannot set color of a non-leather armor item.")
        meta.setColor(color)
        item.itemMeta = meta
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val meta = item.itemMeta
        meta.isUnbreakable = unbreakable
        item.itemMeta = meta
        return this
    }

    fun build(): ItemStack {
        return item.clone()
    }

    companion object {
        @JvmStatic
        fun of(material: Material): ItemBuilder {
            return ItemBuilder(material, 1)
        }

        @JvmStatic
        fun of(material: Material, amount: Int): ItemBuilder {
            return ItemBuilder(material, amount)
        }

        @JvmStatic
        fun copyOf(builder: ItemBuilder): ItemBuilder {
            return ItemBuilder(builder.build())
        }

        @JvmStatic
        fun copyOf(item: ItemStack): ItemBuilder {
            return ItemBuilder(item)
        }
    }
}