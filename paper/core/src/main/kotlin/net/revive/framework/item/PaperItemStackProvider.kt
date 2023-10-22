package net.revive.framework.item

import net.revive.framework.annotation.inject.AutoBind
import org.bukkit.inventory.ItemStack

@AutoBind
class PaperItemStackProvider : AbstractItemStackProvider<PaperFrameworkItemStack> {
    override fun getEmpty(): PaperFrameworkItemStack {
        return EMPTY
    }

    companion object {
        val EMPTY = PaperFrameworkItemStack(ItemStack.empty())
    }
}