package net.mystoria.framework.command.impl

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.command.FrameworkCommand
import org.bukkit.command.CommandSender

@AutoRegister
@CommandAlias("framework")
@CommandPermission("framework.command.admin")
object FrameworkCommand : FrameworkCommand() {

    @Subcommand("test-sentry")
    fun testSentry(sender: CommandSender) {
        throw RuntimeException("This is a test of the auto sentry system.")
    }
}