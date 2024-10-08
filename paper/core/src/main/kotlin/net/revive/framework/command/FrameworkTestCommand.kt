package net.revive.framework.command

import co.aikar.commands.CommandHelp
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.annotation.command.AutoRegister
import net.revive.framework.config.SecurityConfig
import net.revive.framework.constants.Deployment
import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.menu.callback.impl.ColorCallbackMenu
import net.revive.framework.menu.openMenu
import net.revive.framework.menu.openMenuTemplate
import net.revive.framework.menu.template.MenuTemplateService
import net.revive.framework.menu.test.TestMenu
import net.revive.framework.scoreboard.IScoreboard
import net.revive.framework.scoreboard.ScoreboardService
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.pvc
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("framework")
@CommandPermission("framework.command.admin")
object FrameworkTestCommand : FrameworkCommand() {

    @Inject
    lateinit var scoreboardService: ScoreboardService

    @Inject
    lateinit var config: SecurityConfig

    @HelpCommand
    @Default
    override fun help(help: CommandHelp) = help.showHelp()

    @Subcommand("test-component")
    fun testComponent(sender: CommandSender) {
        sender.sendMessage(buildComponent {
            text("test ", Tailwind.RED_400)
            text("this is a test of the component builder frfr", Tailwind.LIME_600)
            text("you are ", Tailwind.GRAY_100)
            if (sender is Player) {
                append(sender.displayName())
            } else {
                text("CONSOLE", Tailwind.ROSE_900)
            }
        })
    }

    @Subcommand("config-security")
    fun config(sender: CommandSender) {
        sender.sendMessage(net.revive.framework.Framework.useWithReturn {
            it.serializer.serialize(config)
        })
    }

    @Subcommand("test-sentry")
    fun testSentry(sender: CommandSender) {
        sender.sendMessage(buildComponent("testing sentry you will get error dw.", Tailwind.GREEN_400))
        throw RuntimeException("This is a test of the auto sentry system.")
    }

    @Subcommand("test-menu")
    fun testMenu(player: Player) {
        player.pvc.openMenu(TestMenu())
    }

    @Subcommand("asf")
    fun msg(player: Player) {

    }

    @Subcommand("test-menu-color")
    fun testMenuColor(player: Player) {
        player.pvc.openMenu(ColorCallbackMenu {
            player.sendMessage(buildComponent("You clicked this color $it", it))
        })
    }

    @Subcommand("test-menu-template")
    fun testMenu(player: Player, @Name("id") id: String) {
        player.pvc.openMenuTemplate(
            MenuTemplateService.templates[id.lowercase()]
                ?: throw ConditionFailedException("Unable to find a menu with that template id.")
        )
    }
}