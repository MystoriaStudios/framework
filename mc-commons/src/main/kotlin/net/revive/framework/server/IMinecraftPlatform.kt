package net.revive.framework.server

import net.revive.framework.sender.AbstractFrameworkPlayer
import java.util.UUID

interface IMinecraftPlatform {
    fun getOnlinePlayers(): MutableList<FrameworkPlayer>
    fun getPlayer(id: UUID): FrameworkPlayer?
}