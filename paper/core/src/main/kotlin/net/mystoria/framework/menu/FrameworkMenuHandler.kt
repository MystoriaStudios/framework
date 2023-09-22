package net.mystoria.framework.menu

import net.mystoria.framework.Framework
import net.mystoria.framework.flavor.annotation.Inject
import net.mystoria.framework.menu.button.IButton
import net.mystoria.framework.nms.INMSVersion
import net.mystoria.framework.utils.ItemStackBuilder
import net.mystoria.framework.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class FrameworkMenuHandler : IMenuHandler {

    @Inject
    lateinit var nmsVersion: INMSVersion

    @Inject
    lateinit var menuService: MenuService

    /**
     * Creates an inventory for the given player and menu.
     *
     * @param player The player for whom the inventory is created.
     * @param menu The menu for which the inventory is created.
     * @return The created inventory.
     */
    override fun createInventory(player: Player, menu: IMenu): Inventory {
        val title = menu.getTitle(player)
        val invButtons = menu.getButtons(player)

        val size = menu.size(invButtons)

        return Bukkit.createInventory(null, size, title).apply {
            invButtons.forEach { invButton ->
                if (invButton.key >= size) {
                    return@forEach
                }

                menu.metaData.buttons[invButton.key] = invButton.value
                setItem(invButton.key, constructItemStack(player, invButton.value))
            }
        }
    }

    /**
     * Opens the specified menu for the given player.
     *
     * @param player The player who is opening the menu.
     * @param menu The menu to be opened.
     */
    override fun openMenu(player: Player, menu: IMenu) = Tasks.async {
        runCatching {
            openCustomInventory(player, createInventory(player, menu), menu)
        }.onFailure { throwable ->
            Framework.use {
                it.sentryService.log(throwable) { id ->
                    var message = "${ChatColor.RED}Whoops! we ran into an error whilst trying to do that. "
                    message += if (id != null) {
                        "Please report the following error code to a platform administrator ${ChatColor.YELLOW}$id"
                    } else "Please try again later."
                }
            }
        }
    }

    /**
     * Opens a custom inventory for the given player.
     *
     * @param player The player for whom the custom inventory is opened.
     * @param inventory The custom inventory to be opened.
     * @param menu The menu that the inventory is being opened.
     */
    override fun openCustomInventory(player: Player, inventory: Inventory, menu: IMenu) {
        val openInventory = player.openInventory

        if (openInventory != null)
        {
            // check if top inv size is the same as new menu size and if the titles match
            if (openInventory.topInventory != null && openInventory.topInventory.size == inventory.size && openInventory.title() == menu.getTitle(player))
            {
                openInventory.topInventory.contents = inventory.contents
                return
            }

            menu.metaData.manualClose = false
        }

        if (Bukkit.isPrimaryThread()) {
            nmsVersion.menuHandler.openCustomInventory(player, inventory, inventory.size)
            updateMenu(player, menu)

            player.updateInventory()
            return
        }

        nmsVersion.menuHandler.openCustomInventory(player, inventory, inventory.size)
        updateMenu(player, menu)

        player.updateInventory()
    }

    /**
     * Updates the specified menu for the given player.
     *
     * @param player The player for whom the menu is updated.
     * @param menu The menu to be updated.
     */
    override fun updateMenu(player: Player, menu: IMenu) {
        menuService.addOpenedMenu(player, menu)

        menu.metaData.closed = false
        menu.onOpen(player)
    }

    /**
     * Retrieves the slot number based on the provided X and Y coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return The slot number corresponding to the coordinates.
     */
    override fun getSlot(x: Int, y: Int): Int {
        return 9 * y + x
    }

    /**
     * Constructs an ItemStack for a button within the given player's context.
     *
     * @param player The player for whom the ItemStack is constructed.
     * @param button The button for which the ItemStack is constructed.
     * @return The constructed ItemStack.
     */
    override fun constructItemStack(player: Player, button: IButton): ItemStack {
        // TODO: Make work with if not pack shi
        val type = button.getMaterial(player)
        return ItemStackBuilder().apply {
            button.getButtonItem(player).invoke(this)
            type(type.parseMaterial()!!)

            // TODO: only apply if they are using resource pack
            button.applyTexture(player).invoke(this)
        }.build()

    }
}