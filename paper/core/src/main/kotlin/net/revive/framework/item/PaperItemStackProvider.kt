package net.revive.framework.item

import net.revive.framework.annotation.inject.AutoBind
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@AutoBind
class PaperItemStackProvider : IItemStackProvider<PaperFrameworkItemStack> {
    override fun getEmpty(): PaperFrameworkItemStack {
        return EMPTY
    }

    companion object {
        val EMPTY = PaperFrameworkItemStack(ItemStack(Material.AIR, 0))
    }
}