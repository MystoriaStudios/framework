package net.mystoria.framework.menu
import net.mystoria.framework.menu.button.IButton
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

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
     * @param menu The menu that the inventory is being opened.
     */
    fun openCustomInventory(player: Player, inventory: Inventory, menu: IMenu)

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
     * Constructs an ItemStack for a button within the given player's context.
     *
     * @param player The player for whom the ItemStack is constructed.
     * @param button The button for which the ItemStack is constructed.
     * @return The constructed ItemStack.
     */
    fun constructItemStack(player: Player, button: IButton): ItemStack
}