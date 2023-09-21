package net.mystoria.framework.menu

import net.mystoria.framework.menu.button.IButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

/**
 * An interface representing a menu handler for managing in-game menus and inventories.
 */
interface IMenuHandler {

    /**
     * Creates an inventory for the given player and menu.
     *
     * @param player The player for whom the inventory is created.
     * @param menu The menu for which the inventory is created.
     * @return The created inventory.
     */
    fun createInventory(player: Player, menu: IMenu): Inventory

    /**
     * Opens the specified menu for the given player.
     *
     * @param player The player who is opening the menu.
     * @param menu The menu to be opened.
     */
    fun openMenu(player: Player, menu: IMenu)

    /**
     * Opens a custom inventory for the given player.
     *
     * @param player The player for whom the custom inventory is opened.
     * @param inventory The custom inventory to be opened.
     */
    fun openCustomInventory(player: Player, inventory: Inventory)

    /**
     * Retrieves the inventory type for a given size.
     *
     * @param size The size for which the inventory type is determined.
     * @return The corresponding inventory type.
     */
    fun getWindowType(size: Int): InventoryType

    /**
     * Updates the specified menu for the given player.
     *
     * @param player The player for whom the menu is updated.
     * @param menu The menu to be updated.
     */
    fun updateMenu(player: Player, menu: IMenu)

    /**
     * Retrieves the slot number based on the provided X and Y coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return The slot number corresponding to the coordinates.
     */
    fun getSlot(x: Int, y: Int): Int

    /**
     * Asynchronously loads resources for the player and invokes the callback with a boolean
     * indicating the success of the resource loading.
     *
     * @param player The player for whom resources are loaded.
     * @param callback A callback function to be invoked with a boolean result.
     */
    fun asyncLoadResources(player: Player, callback: (Boolean) -> Unit)

    /**
     * Calculates the size of the menu based on the provided buttons and menu configuration.
     *
     * @param menu The menu for which the size is calculated.
     * @param buttons A map of button positions to button identifiers.
     * @return The size of the menu.
     */
    fun size(menu: IMenu, buttons: Map<Int, IButton>): Int
}