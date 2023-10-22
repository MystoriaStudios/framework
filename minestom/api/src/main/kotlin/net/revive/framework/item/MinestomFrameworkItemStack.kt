package net.revive.framework.item

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class MinestomFrameworkItemStack(item: ItemStack) : AbstractFrameworkItemStack<ItemStack>(item) {

    override fun setType(material: Key) {
        Material.fromNamespaceId(material.toString())?.let { item.withMaterial(it) }
    }

    override fun setAmount(amount: Int) {
        item.withAmount(amount)
    }

    override fun setName(name: Component) {
        item.withDisplayName(name)
    }

    override fun addLore(newLore: List<Component>) {
        item.lore.addAll(newLore)
    }

    override fun setLore(newLore: List<Component>) {
        item.withLore(newLore)
    }

    override fun setDurability(durability: Int) {

    }

    override fun setCustomModelData(modelData: Int) {
        // TODO: IMPLEMENT THESE
    }

    override fun addFlag(flag: FrameworkItemFlag) {
    }

    override fun setColor(color: TextColor) {
    }
}
