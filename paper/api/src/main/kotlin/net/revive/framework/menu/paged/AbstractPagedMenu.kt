package net.revive.framework.menu.paged

import net.kyori.adventure.text.Component
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.MenuService
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.distribution.MenuDistributionRow
import org.bukkit.entity.Player
import kotlin.math.ceil
/**
 * An abstract class representing a paged menu that extends the IMenu interface.
 */
abstract class AbstractPagedMenu : IMenu {

    // The current page number.
    var page: Int = 1

    // The distribution type for menu buttons.
    private var distribution = MenuDistributionRow.NONE

    /**
     * Retrieves the title of the menu with pagination for the given player.
     *
     * @param player The player for whom the title is generated.
     * @return The paginated menu title as a string.
     */
    open fun getPrePaginatedTitle(player: Player): Component? = null

    /**
     * Retrieves the title of the menu without pagination for the given player.
     *
     * @param player The player for whom the title is generated.
     * @return The unpaginated menu title as a component.
     */
    open fun getUnPaginatedTitle(player: Player): Component {
        return Component.empty()
    }

    /**
     * Overrides the getTitle method to add pagination information to the menu title.
     *
     * @param player The player for whom the title is generated.
     * @return The formatted menu title with pagination.
     */
    override fun getTitle(player: Player): Component {
        with(getPrePaginatedTitle(player)) {
            if (this != null) {
                val pages = getPages(player)

                return this.append(Component.text(
                    if (pages > 1) " ($page/$pages)" else ""
                ))
            } else {
                return getUnPaginatedTitle(player)
            }
        }
    }

    /**
     * Modifies the current page number and updates the menu.
     *
     * @param player The player performing the page modification.
     * @param mod The amount by which to modify the current page.
     */
    fun modPage(player: Player, mod: Int) {
        page += mod
        metaData.buttons.clear()
        MenuService.menuHandler.openMenu(player, this)
    }

    /**
     * Calculates the total number of pages based on button count.
     *
     * @param player The player for whom the page count is determined.
     * @return The total number of pages, or -1 if no buttons are available.
     */
    fun getPages(player: Player): Int {
        val buttonAmount = getAllPagesButtons(player).size

        return if (buttonAmount == 0) {
            -1
        } else {
            ceil(buttonAmount / maxItemsPerPage.toDouble()).toInt()
        }
    }

    /**
     * Overrides the getButtons method to generate paginated menu buttons.
     *
     * @param player The player for whom the buttons are generated.
     * @return A map of button positions to button identifiers.
     */
    override fun getButtons(player: Player): Map<Int, IButton> {
        val buttons = mutableMapOf<Int, IButton>()

        // Handle pagination buttons.
        val pageButtonSlots = pageButtonSlots
        buttons[pageButtonSlots.first] = PageButton(-1, this)
        buttons[pageButtonSlots.second] = PageButton(1, this)

        val buttonSlots = getAllPagesButtonSlots()
            .ifEmpty {
                this.distribution.allDistributed(size(buttons) / 9, buttons)
            }

        if (buttonSlots.isEmpty()) {
            val minIndex = ((page - 1) * maxItemsPerPage.toDouble()).toInt()
            val maxIndex = (page * maxItemsPerPage.toDouble()).toInt()

            for (entry in getAllPagesButtons(player).entries) {
                var ind = entry.key
                if (ind in minIndex..maxIndex) {
                    ind -= (maxItemsPerPage * (page - 1).toDouble()).toInt() - 9
                    buttons[buttonStartOffset + ind] = entry.value
                }
            }
        } else {
            val maxPerPage = buttonSlots.size

            val minIndex = (page - 1) * maxPerPage
            val maxIndex = page * maxPerPage

            for ((index, entry) in getAllPagesButtons(player).entries.withIndex()) {
                if (index in minIndex until maxIndex) {
                    buttons[buttonSlots[index - minIndex]] = entry.value
                }
            }
        }

        // Add global buttons, if provided.
        val global = getGlobalButtons(player)
        if (global != null) {
            for ((key, value) in global) {
                buttons[key] = value
            }
        }

        return buttons
    }

    /**
     * Retrieves global buttons that appear on every page of the menu.
     *
     * @param player The player for whom the global buttons are generated.
     * @return A map of button positions to button identifiers, or null if no global buttons are provided.
     */
    open fun getGlobalButtons(player: Player): Map<Int, IButton>? = null

    /**
     * Retrieves a map of button positions to button identifiers for all pages of the menu.
     *
     * @param player The player for whom the buttons are generated.
     * @return A map of button positions (slot numbers) to button identifiers (IButton) for all pages.
     */
    abstract fun getAllPagesButtons(player: Player): Map<Int, IButton>

    /**
     * Abstract property representing the maximum number of items per page.
     */
    abstract val maxItemsPerPage: Int

    /**
     * Abstract property representing the starting button offset.
     */
    abstract val buttonStartOffset: Int

    /**
     * Optional property to specify the button slots for page navigation.
     */
    open val pageButtonSlots: Pair<Int, Int> = Pair(0, 8)

    /**
     * Optional method to specify custom button slots for all pages.
     *
     * @return A list of custom button slots, or an empty list for automatic distribution.
     */
    open fun getAllPagesButtonSlots(): List<Int> = emptyList()
}