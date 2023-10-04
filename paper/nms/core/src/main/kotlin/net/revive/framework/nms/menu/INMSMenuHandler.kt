package net.revive.framework.nms.menu

interface INMSMenuHandler {
    fun openCustomInventory(player: Any, inventory: Any, size: Int)

    fun isSameInventory(inventory: Any, openInventory: Any, title: Any): Boolean
}