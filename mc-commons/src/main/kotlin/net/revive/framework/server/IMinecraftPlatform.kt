package net.revive.framework.server

import net.revive.framework.sender.FrameworkPlayer
import java.util.*

interface IMinecraftPlatform {
    fun getOnlinePlayers(): MutableList<FrameworkPlayer>
    fun getPlayer(id: UUID): FrameworkPlayer?
}