package net.revive.framework.server

import net.revive.framework.sender.FrameworkPlayer
import java.util.UUID

interface IMinecraftPlatform {
    fun getOnlinePlayers(): MutableList<FrameworkPlayer>
    fun getPlayer(id: UUID): FrameworkPlayer?
}