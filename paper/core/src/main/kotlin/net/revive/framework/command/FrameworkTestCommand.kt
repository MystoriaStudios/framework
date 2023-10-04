package net.revive.framework.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.Framework
import net.revive.framework.annotation.command.AutoRegister
import net.revive.framework.config.SecurityConfig
import net.revive.framework.constants.Deployment
import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.menu.IMenuHandler
import net.revive.framework.menu.test.TestMenu
import net.revive.framework.scoreboard.IScoreboard
import net.revive.framework.scoreboard.ScoreboardService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("framework")
@CommandPermission("framework.command.admin")
object FrameworkTestCommand : FrameworkCommand() {

    @Inject lateinit var scoreboardService: ScoreboardService
    @Inject lateinit var menuHandler: IMenuHandler
    @Inject lateinit var config: SecurityConfig

    @Subcommand("config-security")
    fun config(sender: CommandSender) {
        sender.sendMessage(net.revive.framework.Framework.useWithReturn {
            it.serializer.serialize(config)
        })
    }

    @Subcommand("test-sentry")
    fun testSentry(sender: CommandSender) {
        throw RuntimeException("This is a test of the auto sentry system.")
    }

    @Subcommand("test-scoreboard")
    fun testScoreboard(player: Player) {
        if (scoreboardService.primaryScoreboard != null) throw RuntimeException("There is already a primary scoreboard provider.")

        scoreboardService.updatePrimaryProvider(object : IScoreboard {
            override fun getTitle(player: Player) : Component {
                return Component
                    .text(Deployment.SERVER_NAME)
                    .style(Style
                        .style(TextDecoration.BOLD)
                        .color(TextColor.fromHexString(Tailwind.ORANGE_400))
                    )
            }

            override fun getScores(player: Player): List<Component> {
                return listOf(
                    Component.empty(),
                    Component.text("this is a test of").color(TextColor.fromHexString(Tailwind.GRAY_100)),
                    Component.text("the scoreboard provider").color(TextColor.fromHexString(Tailwind.GRAY_100)),
                    Component.text("built into framework").color(TextColor.fromHexString(Tailwind.GRAY_100)),
                    Component.empty(),
                    Component.text(Deployment.WEBSITE_URL).color(TextColor.fromHexString(Tailwind.AMBER_300)),
                )
            }
        })

        Bukkit.getServer().onlinePlayers.forEach {
            scoreboardService.refresh(it)
        }
    }

    @Subcommand("test-menu")
    fun testMenu(player: Player) {
        val menu = TestMenu()
        menuHandler.openMenu(player, menu)
    }
}