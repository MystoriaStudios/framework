package net.revive.framework.menu

import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Service
import net.revive.framework.menu.button.IButton
import net.revive.framework.nms.menu.INMSMenuHandler
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.buildComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.jvm.Throws

@Service
object FrameworkMenuHandler : IMenuHandler {

    @Inject lateinit var menuService: MenuService
    @Inject lateinit var nmsMenuHandler: INMSMenuHandler

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
            net.revive.framework.Framework.use {
                it.sentryService.log(throwable) { id ->
                    var message = "Whoops! we ran into an error whilst trying to do that. "
                    message += if (id != null) {
                        "Please report the following error code to a platform administrator $id"
                    } else "Please try again later."
                    player.sendMessage(buildComponent(message, Tailwind.RED_600))
                    if (id == null) {
                        throwable.printStackTrace()
                        if (player.isOp) {
                            throwable.stackTrace.forEach {
                                player.sendMessage(buildComponent(it.toString(), Tailwind.RED_400))
                            }
                        }
                    }
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

        // check if top inv size is the same as new menu size and if the titles match
        if (nmsMenuHandler.isSameInventory(inventory, openInventory, menu.getTitle(player))) {
            openInventory.topInventory.contents = inventory.contents
            return
        }

        menu.metaData.manualClose = false

        if (Bukkit.isPrimaryThread()) {
            player.openInventory(inventory)
            updateMenu(player, menu)

            player.updateInventory()
            return
        }


        Tasks.delayed(1L)
        {
            player.openInventory(inventory)
            updateMenu(player, menu)

            player.updateInventory()
        }
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
            type(type.parseMaterial() ?: Material.DIAMOND_SWORD)
            button.getButtonItem(player).invoke(this)

            // TODO: only apply if they are using resource pack
            button.applyTexture(player).invoke(this)
        }.build()

    }
}