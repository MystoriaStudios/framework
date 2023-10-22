package net.revive.framework.menu

import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Service
import net.revive.framework.item.FrameworkItemStack
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.item.PaperFrameworkItemStack
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.inventory.FrameworkInventory
import net.revive.framework.menu.inventory.PaperFrameworkInventory
import net.revive.framework.nms.menu.INMSMenuHandler
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.PaperFrameworkPlayer

import net.revive.framework.utils.Tasks
import net.revive.framework.utils.buildComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Service
object FrameworkMenuHandler : IMenuHandler {

    @Inject
    lateinit var menuService: MenuService

    @Inject
    lateinit var nmsMenuHandler: INMSMenuHandler

    /**
     * Creates an inventory for the given player and menu.
     *
     * @param player The player for whom the inventory is created.
     * @param menu The menu for which the inventory is created.
     * @return The created inventory.
     */
    override fun createInventory(player: FrameworkPlayer, menu: IMenu): FrameworkInventory {
        val title = menu.getTitle(player)
        val invButtons = menu.getButtons(player)

        val size = menu.size(invButtons)

        return PaperFrameworkInventory(Bukkit.createInventory(null, size, title).apply {
            invButtons.forEach { invButton ->
                if (invButton.key >= size) {
                    return@forEach
                }

                menu.metaData.buttons[invButton.key] = invButton.value
                setItem(invButton.key, constructItemStack(player, invButton.value).item as ItemStack)
            }
        })
    }

    /**
     * Opens the specified menu for the given player.
     *
     * @param player The player who is opening the menu.
     * @param menu The menu to be opened.
     */
    override fun openMenu(player: FrameworkPlayer, menu: IMenu) = Tasks.async {
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
                        if ((player as PaperFrameworkPlayer).player.isOp) {
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
    override fun openCustomInventory(player: FrameworkPlayer, inventory: FrameworkInventory, menu: IMenu) {
        val bukkitPlayer = (player as PaperFrameworkPlayer).player
        val openInventory = bukkitPlayer.openInventory

        // check if top inv size is the same as new menu size and if the titles match
        if (nmsMenuHandler.isSameInventory(inventory, openInventory, menu.getTitle(player))) {
            openInventory.topInventory.contents = (inventory.inventory as Inventory).contents
            return
        }

        menu.metaData.manualClose = false

        if (Bukkit.isPrimaryThread()) {
            bukkitPlayer.openInventory(inventory.inventory as Inventory)
            updateMenu(player, menu)

            bukkitPlayer.updateInventory()
            return
        }


        Tasks.delayed(1L)
        {
            bukkitPlayer.openInventory(inventory.inventory as Inventory)
            updateMenu(player, menu)

            bukkitPlayer.updateInventory()
        }
    }

    /**
     * Updates the specified menu for the given player.
     *
     * @param player The player for whom the menu is updated.
     * @param menu The menu to be updated.
     */
    override fun updateMenu(player: FrameworkPlayer, menu: IMenu) {
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
    override fun constructItemStack(player: FrameworkPlayer, button: IButton): FrameworkItemStack {
        // TODO: Make work with if not pack shi
        val type = button.getMaterial(player)

        return ItemStackBuilder(PaperFrameworkItemStack(ItemStack(Material.matchMaterial(type.toString()) ?: Material.BARRIER))).apply {
            button.getButtonItem(player).invoke(this)

            // TODO: only apply if they are using resource pack
            button.applyTexture(player).invoke(this)
        }.build()
    }
}