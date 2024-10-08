package net.revive.framework.menu

import co.aikar.commands.ConditionFailedException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.annotation.Listeners
import net.revive.framework.constants.Tailwind
import net.revive.framework.event.event
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.pvc
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

@Listeners
object FrameworkMenuListeners : Listener {

    @Inject
    lateinit var menuHandler: IMenuHandler

    @Inject
    lateinit var menuService: MenuService

    @EventHandler(priority = EventPriority.MONITOR)
    fun onClose(event: InventoryCloseEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        val frameworkPlayer = player.pvc

        if (menuService.getOpenedMenu(frameworkPlayer) != null) {
            menuService.removeOpenedMenu(frameworkPlayer)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryDrag(event: InventoryDragEvent) = event(event.whoClicked) {
        if (event.whoClicked !is Player) return
        menuService.getOpenedMenu((event.whoClicked as Player).pvc) ?: return

        if (event.inventory != event.view.topInventory) {
            event.isCancelled = true
            return
        }

        if (event.newItems.maxByOrNull { it.key }!!.key >= event.view.topInventory.size) {
            event.isCancelled = true
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun onButtonPress(event: InventoryClickEvent) = event(event.whoClicked) {
        val player = event.whoClicked as Player
        val frameworkPlayer = player.pvc

        val openMenu = menuService.getOpenedMenu(frameworkPlayer)
        if (openMenu != null) {
            if (event.click == ClickType.DOUBLE_CLICK) {
                event.isCancelled = true
                return
            }

            // handle bottom mechanics (edit own inventory)
            if (!openMenu.cancelLowerClicks) {
                if (event.clickedInventory != event.view.topInventory) {
                    event.isCancelled = true
                    return
                }
            }

            if (menuService.clickCooldown.containsKey(player.uniqueId)) {
                if (System.currentTimeMillis() < menuService.clickCooldown[player.uniqueId]!!) {
                    event.isCancelled = true
                    return
                }
            }

            // handle items being inserted via cursor
            if (!event.cursor.isEmpty && event.cursor.type != Material.AIR && (event.click == ClickType.LEFT || event.click == ClickType.RIGHT || event.click == ClickType.MIDDLE)) {
                if (event.clickedInventory == event.view.topInventory) {
                    event.isCancelled = true

                    /* TODO
                    when (event.click) {
                        ClickType.LEFT -> {
                            event.cursor
                        }

                        ClickType.RIGHT -> {
                            ItemBuilder.copyOf(event.cursor).amount(1).build()
                        }

                        ClickType.MIDDLE -> {
                            val half = (event.cursor.amount / 2).coerceAtLeast(1)
                            ItemBuilder.copyOf(event.cursor).amount(half).build()
                        }

                        else -> {
                            event.cursor
                        }
                    }

                     */
                    return
                }
            }

            // handle items being inserted via shift-clicking
            if (event.slot != event.rawSlot) {
                if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
                    event.isCancelled = true
                }
                return
            }

            // handle button
            if (openMenu.metaData.buttons.containsKey(event.slot)) {
                val button = openMenu.metaData.buttons[event.slot]!!

                event.isCancelled = true


                try {
                    button.onClick(frameworkPlayer, event.click.pvc)
                } catch (exception: ConditionFailedException) {
                    player.sendMessage(
                        Component.text(exception.localizedMessage).color(TextColor.fromHexString(Tailwind.RED_700))
                    )
                }

                // check if player is still in the same menu and needs to update
                if (menuService.hasOpenedMenu(frameworkPlayer)) {
                    val newMenu = menuService.getOpenedMenu(frameworkPlayer)
                    if (newMenu === openMenu && newMenu.updateOnClick) {
                        menuHandler.openMenu(frameworkPlayer, newMenu)
                    }
                }

                if (event.isCancelled) {
                    Tasks.delayed(1L) { player.updateInventory() }
                }

                return
            } else {
                if (event.clickedInventory == event.view.topInventory) {
                    event.isCancelled = true
                }
            }

            // handle non-cancelling menu
            if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
                event.isCancelled = true
            }
        }
    }
}