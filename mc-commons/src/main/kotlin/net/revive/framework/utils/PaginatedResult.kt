package net.revive.framework.utils

import co.aikar.commands.ConditionFailedException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.constants.Icons
import net.revive.framework.constants.Tailwind
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.sender.FrameworkSender

/**
 * Abstract class to handle paginated results. It's responsible for generating and displaying paginated content
 * to a given sender, including navigational elements if appropriate.
 *
 * @param T Type of the entries to paginate.
 * @property resultsPerPage Number of results to display per page. Defaults to 20.
 */
abstract class PaginatedResult<T>(private val resultsPerPage: Int = 20) {

    /**
     * Displays the paginated results to a command sender using a collection of results.
     *
     * @param sender The command sender to which the results will be displayed.
     * @param results Collection of results to display.
     * @param page The desired page number to display.
     */
    fun display(sender: FrameworkSender<*>, results: Collection<T>, page: Int) {
        this.display(sender, ArrayList(results), page)
    }

    /**
     * Displays the paginated results to a command sender using a list of results.
     *
     * @param sender The command sender to which the results will be displayed.
     * @param results List of results to display.
     * @param page The desired page number to display.
     * @param command An optional command string used for clickable navigation elements.
     *                If provided, navigation arrows will be clickable. If not, they will only be indicative.
     */
    fun display(sender: FrameworkSender<*>, results: List<T>, page: Int, command: String? = null) {
        if (results.isEmpty()) throw ConditionFailedException("No entries were found.")

        val maxPages = results.size / this.resultsPerPage + 1

        if (page <= 0 || page > maxPages) throw ConditionFailedException("Page '$page' was not found. (§e1 - $maxPages§c)")

        sender.sendMessage(this.getHeader(page, maxPages))

        var i = this.resultsPerPage * (page - 1)
        while (i < this.resultsPerPage * page && i < results.size) {
            sender.sendMessage(format(results[i], i))
            ++i
        }

        if (command != null) {
            if (sender is AbstractFrameworkPlayer<*>) {
                val component = Component.text()

                component.append(
                    Component
                        .text(Icons.DOUBLE_ARROW_LEFT.repeat(2))
                        .color(
                            TextColor.fromHexString(
                                Strings.xor(
                                    page == 1,
                                    Tailwind.RED_600 to Tailwind.EMERALD_400
                                )
                            )
                        ).also {
                            if (page != 1) {
                                it.hoverEvent(
                                    HoverEvent.showText(
                                        Component
                                            .text("Click to view page ${page - 1}")
                                            .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                                    )
                                )
                                it.clickEvent(ClickEvent.runCommand(String.format("/${command}", page - 1)))
                            }
                        }
                )

                component.append(
                    Component
                        .text("Page $page out of $maxPages ")
                        .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                )
                component.append(
                    Component
                        .text("(${Strings.pluralize(results.size, "result")})")
                        .color(TextColor.fromHexString(Tailwind.GRAY_500))
                )

                component.append(
                    Component
                        .text(Icons.DOUBLE_ARROW_RIGHT.repeat(2))
                        .color(
                            TextColor.fromHexString(
                                Strings.xor(
                                    page == maxPages,
                                    Tailwind.RED_600 to Tailwind.EMERALD_400
                                )
                            )
                        ).also {
                            if (page != maxPages) {
                                it.hoverEvent(
                                    HoverEvent.showText(
                                        Component
                                            .text("Click to view page ${page + 1}")
                                            .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                                    )
                                )
                                it.clickEvent(ClickEvent.runCommand(String.format("/${command}", page + 1)))
                            }
                        }
                )

                sender.sendMessage(component.build())
            } else {
                val component = Component.text()
                component.append(
                    Component
                        .text("Page $page out of $maxPages ")
                        .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                )

                component.append(
                    Component
                        .text("(${Strings.pluralize(results.size, "result")})")
                        .color(TextColor.fromHexString(Tailwind.GRAY_500))
                )

                sender.sendMessage(component.build())
            }
        }
    }

    /**
     * Returns a header component to be displayed at the top of the paginated results.
     *
     * @param page The current page number.
     * @param maxPages The total number of available pages.
     * @return The header component.
     */
    abstract fun getHeader(page: Int, maxPages: Int): Component

    /**
     * Transforms a result into a displayable component format. This function should be implemented to
     * define how each entry in the results list will be displayed.
     *
     * @param result The result entry to format.
     * @param resultIndex The index of the result in the list, used if indexing is desired in the display format.
     * @return The result entry as a formatted component.
     */
    abstract fun format(result: T, resultIndex: Int): Component
}
