package net.mystoria.framework.utils

import co.aikar.commands.ConditionFailedException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.mystoria.framework.constants.Icons
import net.mystoria.framework.constants.Tailwind
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PaginatedResult<T>(private val resultsPerPage: Int = 20) {

    fun display(sender: CommandSender, results: Collection<T>, page: Int) {
        this.display(sender, ArrayList(results), page)
    }

    fun display(sender: CommandSender, results: List<T>, page: Int, command: String? = null) {
        if (results.isEmpty()) throw ConditionFailedException("No entries were found.")

        val maxPages = results.size / this.resultsPerPage + 1

        if (page <= 0 || page > maxPages) throw ConditionFailedException("Page '" + page + "' was not found. (" + ChatColor.YELLOW + "1 - " + maxPages + ChatColor.RED + ")")

        sender.sendMessage(this.getHeader(page, maxPages))

        var i = this.resultsPerPage * (page - 1)
        while (i < this.resultsPerPage * page && i < results.size) {
            sender.sendMessage(format(results[i], i))
            ++i
        }

        if (command != null) {
            if (sender is Player) {
                val component = Component.text()

                component.append(
                    Component
                        .text(Icons.DOUBLE_ARROW_LEFT.repeat(2))
                        .color(TextColor.fromHexString(Strings.xor(page == 1, Tailwind.RED_600 to Tailwind.EMERALD_400))).also {
                            if (page != 1) {
                                it.hoverEvent(HoverEvent.showText(Component
                                    .text("Click to view page ${page - 1}")
                                    .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                                ))
                                it.clickEvent(ClickEvent.runCommand(String.format("/${command}", page - 1)))
                            }
                        }
                )

                component.append(Component
                    .text("Page $page out of $maxPages ")
                    .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                )
                component.append(Component
                    .text("(${Strings.pluralize(results.size, "result")})")
                    .color(TextColor.fromHexString(Tailwind.GRAY_500))
                )

                component.append(
                    Component
                        .text(Icons.DOUBLE_ARROW_RIGHT.repeat(2))
                        .color(TextColor.fromHexString(Strings.xor(page == maxPages, Tailwind.RED_600 to Tailwind.EMERALD_400))).also {
                            if (page != maxPages) {
                                it.hoverEvent(HoverEvent.showText(Component
                                    .text("Click to view page ${page + 1}")
                                    .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                                ))
                                it.clickEvent(ClickEvent.runCommand(String.format("/${command}", page + 1)))
                            }
                        }
                )

                sender.sendMessage(component.build())
            } else {
                val component = Component.text()
                component.append(Component
                    .text("Page $page out of $maxPages ")
                    .color(TextColor.fromHexString(Tailwind.EMERALD_400))
                )

                component.append(Component
                    .text("(${Strings.pluralize(results.size, "result")})")
                    .color(TextColor.fromHexString(Tailwind.GRAY_500))
                )

                sender.sendMessage(component.build())
            }
        }
    }

    abstract fun getHeader(page: Int, maxPages: Int): Component
    abstract fun format(result: T, resultIndex: Int): Component
}
