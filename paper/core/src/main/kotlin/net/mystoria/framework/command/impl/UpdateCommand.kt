package net.mystoria.framework.command.impl

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.command.FrameworkCommand

@AutoRegister
@CommandAlias("update")
@CommandPermission("framework.command.updater")
object UpdateCommand : FrameworkCommand() {

    @Subcommand("list")
    fun list() {

    }
}