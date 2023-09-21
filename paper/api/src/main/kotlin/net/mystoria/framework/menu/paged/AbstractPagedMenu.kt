package net.mystoria.framework.menu.paged

import net.mystoria.framework.flavor.annotation.Inject
import net.mystoria.framework.menu.IMenu
import net.mystoria.framework.menu.IMenuHandler
import net.mystoria.framework.menu.MenuService
import net.mystoria.framework.menu.button.IButton
import net.mystoria.framework.menu.distribution.MenuDistributionRow
import org.bukkit.entity.Player
import kotlin.math.ceil

abstract class AbstractPagedMenu : IMenu {

    var page: Int = 1

    var verticalView: Boolean = false
    var applyHeader: Boolean = true

    var distribution = MenuDistributionRow.NONE

    open fun getPrePaginatedTitle(player: Player): String? = null
    open fun getUnPaginatedTitle(player: Player): String {
        return ""
    }

    override fun getTitle(player: Player): String
    {
        with(getPrePaginatedTitle(player)) {
            if (this != null) {
                val pages = getPages(player)

                return "${getPrePaginatedTitle(player)}${
                    if (pages > 1) " ($page/$pages)" else ""
                }"
            } else {
                return getUnPaginatedTitle(player)
            }
        }
    }

    fun modPage(player: Player, mod: Int)
    {
        page += mod
        metaData.buttons.clear()
        MenuService.menuHandler.openMenu(player, this)
    }

    fun getPages(player: Player): Int
    {
        val buttonAmount = getAllPagesButtons(player).size

        return if (buttonAmount == 0) {
            -1
        } else {
            ceil(buttonAmount / maxItemsPerPage.toDouble()).toInt()
        }
    }

    override fun getButtons(player: Player): Map<Int, IButton> {
        val buttons = mutableMapOf<Int, IButton>()


        val pageButtonSlots = pageButtonSlots
        buttons[pageButtonSlots.first] = PageButton(-1, this)
        buttons[pageButtonSlots.second] = PageButton(1, this)

        val buttonSlots = getAllPagesButtonSlots()
            .ifEmpty {
                this.distribution.allDistributed(MenuService.menuHandler.size(this,buttons) / 9, buttons)
            }

        if (buttonSlots.isEmpty())
        {
            val minIndex = ((page - 1) * maxItemsPerPage.toDouble()).toInt()
            val maxIndex = (page * maxItemsPerPage.toDouble()).toInt()

            for (entry in getAllPagesButtons(player).entries)
            {
                var ind = entry.key
                if (ind in minIndex..<maxIndex)
                {
                    ind -= (maxItemsPerPage * (page - 1).toDouble()).toInt() - 9
                    buttons[buttonStartOffset + ind] = entry.value
                }
            }
        } else
        {
            val maxPerPage = buttonSlots.size

            val minIndex = (page - 1) * maxPerPage
            val maxIndex = page * maxPerPage

            for ((index, entry) in getAllPagesButtons(player).entries.withIndex())
            {
                if (index in minIndex until maxIndex)
                {
                    buttons[buttonSlots[index - minIndex]] = entry.value
                }
            }
        }

        val global = getGlobalButtons(player)
        if (global != null)
        {
            for ((key, value) in global)
            {
                buttons[key] = value
            }
        }

        return buttons
    }

    open fun getGlobalButtons(player: Player): Map<Int, IButton>? = null
    abstract fun getAllPagesButtons(player: Player): Map<Int, IButton>

    abstract val maxItemsPerPage: Int
    abstract val buttonStartOffset: Int

    open val pageButtonSlots: Pair<Int, Int> = Pair(0, 8)
    open fun getAllPagesButtonSlots(): List<Int> = emptyList()

}