package net.mystoria.framework.menu

import net.mystoria.framework.menu.button.IButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

interface IMenuHandler {

    fun createInventory(player: Player, menu: IMenu): Inventory
    fun openMenu(player: Player, menu: IMenu)
    fun openCustomInventory(player: Player, inventory: Inventory)
    fun getWindowType(size: Int): InventoryType
    fun updateMenu(player: Player, menu: IMenu)
    fun getSlot(x: Int, y: Int): Int
    fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)
    fun size(menu: IMenu, buttons: Map<Int, IButton>): Int
}