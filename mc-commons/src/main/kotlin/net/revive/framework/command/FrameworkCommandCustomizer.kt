package net.revive.framework.command

import co.aikar.commands.CommandManager
import net.revive.framework.annotation.container.ContainerPreEnable
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.sender.FrameworkSender
import net.revive.framework.server.IMinecraftPlatform

@Service
object FrameworkCommandCustomizer {

    @Inject
    lateinit var commandManager: CommandManager<*, *, *, *, *, *>

    @Inject
    lateinit var minecraftPlatform: IMinecraftPlatform

    @Configure
    fun configure()
    {
        commandManager.setDefaultHelpPerPage(6)
    }
}