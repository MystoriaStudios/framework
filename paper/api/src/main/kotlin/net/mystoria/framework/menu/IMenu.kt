package net.mystoria.framework.menu

import net.mystoria.framework.menu.button.IButton
import org.bukkit.entity.Player

/**
 * An interface representing a menu for a player in a game.
 */
interface IMenu {

    /**
     * Determines whether the menu should auto-update its contents.
     *
     * @return `true` if auto-update is enabled, `false` otherwise.
     */
    val autoUpdate: Boolean
        get() = true

    /**
     * Determines whether the menu should update when clicked by the player.
     *
     * @return `true` if update on click is enabled, `false` otherwise.
     */
    val updateOnClick: Boolean
        get() = false

    /**
     * Retrieves the size of the menu.
     *
     * @return The size of the menu as an integer, or -1 if it is not fixed.
     */
    val size: Int
        get() = -1

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
     * @return The title of the menu as a string.
     */
    fun getTitle(player: Player): String

    /**
     * Retrieves the texture for the menu's inventory for the given player.
     *
     * @param player The player for whom the inventory texture is generated.
     * @return The inventory texture as a string.
     */
    fun getInventoryTexture(player: Player): String = getTitle(player)

    /**
     * Retrieves a mapping of button positions to button identifiers for the menu.
     *
     * @param player The player for whom the button mapping is generated.
     * @return A map of button positions (slot numbers) to button identifiers (IButton).
     */
    fun getButtons(player: Player): Map<Int, IButton>

    /**
     * Called when the menu is opened by a player.
     *
     * @param player The player who opened the menu.
     */
    fun onOpen(player: Player) { }

    /**
     * Called when the menu is closed by a player.
     *
     * @param player The player who closed the menu.
     */
    fun onClose(player: Player) { }
}