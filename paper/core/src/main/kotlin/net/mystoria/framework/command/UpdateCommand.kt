package net.mystoria.framework.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.constants.Tailwind
import net.mystoria.framework.flavor.annotation.Named
import net.mystoria.framework.updater.UpdaterService
import net.mystoria.framework.updater.connection.UpdaterConnector
import net.mystoria.framework.utils.PaginatedResult
import org.bukkit.command.CommandSender

@AutoRegister
@CommandAlias("update")
@CommandPermission("framework.command.updater")
object UpdateCommand : FrameworkCommand() {

    @Subcommand("force")
    fun force(sender: CommandSender) {
        UpdaterService.reload()
        UpdaterConnector.applyPendingUpdates()
    }

    @Subcommand("list")
    fun list(sender: CommandSender, @Named("page") @Default("1") page: Int) {
        UpdatePaginatedResult.display(sender, UpdaterService.discoverable.assets, page, "update list %s")
    }

    private object UpdatePaginatedResult : PaginatedResult<String>() {
        override fun getHeader(page: Int, maxPages: Int) = Component
            .text("Currently tracked assets on this deployment")
            .color(TextColor.fromHexString(Tailwind.EMERALD_400))

        override fun format(result: String, resultIndex: Int) = Component
            .text("$result")
            .color(TextColor.fromHexString(Tailwind.EMERALD_300))
    }
}