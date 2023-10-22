package net.revive.framework.menu

import net.kyori.adventure.text.Component
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.sender.FrameworkPlayer
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

/**
 * An interface representing a menu for a player in a game.
 */
interface IMenu {

    /**
     * Represents the metadata associated with the menu.
     */
    val metaData: MetaData

    /**
     * Determines whether the menu should auto-update its contents.
     *
     * @return `true` if auto-update is enabled, `false` otherwise.
     */
    val autoUpdate: Boolean
        get() = true

    /**
     * The auto-update interval for a certain feature.
     *
     * @return The auto-update interval in milliseconds. A value of -1L indicates that auto-updating is disabled.
     */
    val autoUpdateInterval: Long
        get() = -1L

    /**
     * Determines whether the menu should update when clicked by the player.
     *
     * @return `true` if update on click is enabled, `false` otherwise.
     */
    val updateOnClick: Boolean
        get() = false

    /**
     * Determines whether lower clicks should be canceled when interacting with the menu.
     *
     * @return `true` if canceling lower clicks is enabled, `false` otherwise.
     */
    val cancelLowerClicks: Boolean
        get() = true

    /**
     * Determines whether all clicks should be canceled when interacting with the menu.
     *
     * @return `true` if canceling all clicks is enabled, `false` otherwise.
     */
    val cancelClicks: Boolean
        get() = true

    /**
     * Retrieves the title of the menu for the given player.
     *
     * @param player The player for whom the title is generated.
     * @return The title of the menu as a component.
     */
    fun getTitle(player: FrameworkPlayer): Component

    /**
     * Retrieves the texture for the menu's inventory for the given player.
     *
     * @param player The player for whom the inventory texture is generated.
     * @return The inventory texture as a component.
     */
    fun getInventoryTexture(player: FrameworkPlayer): Component = getTitle(player)

    /**
     * Retrieves a mapping of button positions to button identifiers for the menu.
     *
     * @param player The player for whom the button mapping is generated.
     * @return A map of button positions (slot numbers) to button identifiers (IButton).
     */
    fun getButtons(player: FrameworkPlayer): Map<Int, IButton>

    /**
     * Called when the menu is opened by a player.
     *
     * @param player The player who opened the menu.
     */
    fun onOpen(player: FrameworkPlayer) {}

    /**
     * Called when the menu is closed by a player.
     *
     * @param player The player who closed the menu.
     */
    fun onClose(player: FrameworkPlayer) {}

    /**
     * Calculates and returns the size of the menu based on the buttons provided.
     *
     * @param buttons A map of button positions (slot numbers) to button identifiers (IButton).
     * @return The calculated size of the menu as an integer.
     */
    fun size(buttons: Map<Int, IButton>): Int {
        var highest = 0
        for (buttonValue in buttons.keys) {
            if (buttonValue > highest) {
                highest = buttonValue
            }
        }
        return (ceil((highest + 1) / 9.0) * 9.0).toInt()
    }

    /**
     * A nested class representing metadata for the menu.
     */
    class MetaData {
        /**
         * A ConcurrentHashMap containing button positions and button identifiers.
         */
        var buttons: ConcurrentHashMap<Int, IButton> = ConcurrentHashMap()

        /**
         * Indicates whether the menu was manually closed.
         */
        var manualClose: Boolean = true

        /**
         * Indicates whether the menu is closed.
         */
        var closed: Boolean = false
    }
}