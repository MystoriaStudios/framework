package net.revive.framework

import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.PaperFrameworkPlayer
import net.revive.framework.server.IMinecraftPlatform
import org.bukkit.Bukkit
import java.util.*

object PaperMinecraftPlatform : IMinecraftPlatform {
    override fun getOnlinePlayers(): MutableList<FrameworkPlayer> {
        return Bukkit.getOnlinePlayers()
            .map {
                PaperFrameworkPlayer(it)
            }
            .toMutableList()
    }

    override fun getPlayer(id: UUID): FrameworkPlayer? {
        return Bukkit.getPlayer(id)?.let { PaperFrameworkPlayer(it) }
    }
}