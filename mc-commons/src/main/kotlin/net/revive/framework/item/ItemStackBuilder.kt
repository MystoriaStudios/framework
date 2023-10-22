package net.revive.framework.item

import com.google.common.base.Preconditions
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.component.IFrameworkComponent
import net.revive.framework.flavor.annotation.Inject

inline fun buildItem(itemStack: FrameworkItemStack, builder: ItemStackBuilder.() -> Unit): FrameworkItemStack =
    ItemStackBuilder(itemStack = itemStack).apply(builder).build()

inline fun buildItem(builder: ItemStackBuilder.() -> Unit): FrameworkItemStack = ItemStackBuilder(ItemStackBuilder.itemStackProvider.getEmpty()).apply(builder).build()

class ItemStackBuilder(var itemStack: FrameworkItemStack) {

    fun build(): FrameworkItemStack = itemStack
    fun type(material: Key) = apply { itemStack.setType(material) }
    fun amount(amount: Int) = apply { itemStack.setAmount(amount) }
    fun name(name: Component) = apply { itemStack.setName(name) }

    @JvmName("fLore")
    fun lore(lore: List<IFrameworkComponent>) = apply {
        lore(lore.map(IFrameworkComponent::build))
    }

    @JvmName("fLore")
    fun lore(vararg lore: IFrameworkComponent) = apply {
        lore(lore.map(IFrameworkComponent::build))
    }

    fun lore(lore: List<Component>) = apply { itemStack.addLore(lore) }
    fun lore(vararg lore: Component) = apply { itemStack.addLore(lore.toList()) }
    fun durability(data: Int) = apply { itemStack.setDurability(data) }
    fun model(data: Int) = apply { itemStack.setCustomModelData(data) }
    fun flag(flag: FrameworkItemFlag) = apply { itemStack.addFlag(flag) }
    fun flag(vararg flags: FrameworkItemFlag) = apply { flags.forEach { flag(it) }}
    fun color(color: TextColor) = apply { itemStack.setColor(color) }

    companion object {

        @Inject
        lateinit var itemStackProvider: AbstractItemStackProvider<*>
    }
}

class ItemBuilder(val item: FrameworkItemStack) {
    fun amount(amount: Int) = apply { item.setAmount(amount) }
    fun data(data: Int) = apply { item.setDurability(data) }
    fun flag(flag: FrameworkItemFlag) = apply { item.addFlag(flag) }
    fun name(displayName: Component) = apply { item.setName(displayName) }

    fun build(): FrameworkItemStack {
        return item
    }
}