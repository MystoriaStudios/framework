package net.revive.framework.nms.menu

interface INMSMenuHandler {
    fun openCustomInventory(p: Any, inventory: Any, size: Int)

    fun isSameInventory(inventory: Any, openInventory: Any, title: Any): Boolean
}