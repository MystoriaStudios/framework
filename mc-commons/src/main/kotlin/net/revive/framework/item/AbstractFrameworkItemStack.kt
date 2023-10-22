package net.revive.framework.item

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

typealias FrameworkItemStack = AbstractFrameworkItemStack<*>

abstract class AbstractFrameworkItemStack<I>(val item: I) {
    abstract fun setType(material: Key)
    abstract fun setAmount(amount: Int)
    abstract fun setName(name: Component)
    abstract fun addLore(newLore: List<Component>)
    abstract fun setLore(newLore: List<Component>)
    abstract fun setDurability(durability: Int)
    abstract fun setCustomModelData(modelData: Int)
    abstract fun addFlag(flag: FrameworkItemFlag)
    abstract fun setColor(color: TextColor)
}