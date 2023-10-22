package net.revive.framework.item

import net.minestom.server.item.ItemStack
import net.revive.framework.annotation.inject.AutoBind

@AutoBind
class MinestomItemStackProvider : IItemStackProvider<MinestomFrameworkItemStack> {
    override fun getEmpty(): MinestomFrameworkItemStack {
        return MinestomFrameworkItemStack(EMPTY)
    }

    companion object {
        val EMPTY = ItemStack.AIR
    }
}