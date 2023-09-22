package net.mystoria.framework.command.impl

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.command.FrameworkCommand
import net.mystoria.framework.constants.Deployment
import net.mystoria.framework.constants.Tailwind
import net.mystoria.framework.flavor.annotation.Inject
import net.mystoria.framework.menu.IMenuHandler
import net.mystoria.framework.menu.test.TestMenu
import net.mystoria.framework.scoreboard.IScoreboard
import net.mystoria.framework.scoreboard.ScoreboardService
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("framework")
@CommandPermission("framework.command.admin")
object FrameworkCommand : FrameworkCommand() {

    @Inject lateinit var scoreboardService: ScoreboardService
    @Inject lateinit var menuHandler: IMenuHandler

    @Subcommand("test-sentry")
    fun testSentry(sender: CommandSender) {
        throw RuntimeException("This is a test of the auto sentry system.")
    }

    @Subcommand("test-scoreboard")
    fun testScoreboard(player: Player) {
        if (scoreboardService.primaryScoreboard != null) throw RuntimeException("There is already a primary scoreboard provider.")

        scoreboardService.updatePrimaryProvider(object : IScoreboard {
            override fun getTitle(player: Player) : Component {
                return Component.text(Deployment.SERVER_NAME).color(TextColor.fromHexString(Tailwind.LIME_400))
            }

            override fun getScores(player: Player): List<Component> {
                return listOf(
                    Component.empty(),
                    Component.text().apply {
                        it.append(Component
                            .text("this is a test")
                        )
                    }.build(),
                    Component.empty(),
                )
            }
        })
    }

    @Subcommand("test-menu")
    fun testMenu(player: Player) {
        val menu = TestMenu()
        menuHandler.openMenu(player, menu)
    }
}