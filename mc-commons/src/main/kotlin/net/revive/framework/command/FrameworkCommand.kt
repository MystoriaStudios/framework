package net.revive.framework.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.HelpCommand

open class FrameworkCommand : BaseCommand() {

    // default help command
    @HelpCommand
    open fun help(help: CommandHelp) = help.showHelp()
}