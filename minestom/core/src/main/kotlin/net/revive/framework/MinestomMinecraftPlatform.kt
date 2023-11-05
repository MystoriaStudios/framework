package net.revive.framework

import net.minestom.server.MinecraftServer
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.MinestomFrameworkPlayer
import net.revive.framework.server.IMinecraftPlatform
import java.io.File
import java.util.*

object MinestomMinecraftPlatform : IMinecraftPlatform {
    override fun getNodeIP(): String {
        TODO("Not yet implemented")
    }

    override fun getNodePort(): Int {
        TODO("Not yet implemented")
    }

    override fun getOnlinePlayers(): MutableList<FrameworkPlayer> {
        return MinecraftServer.getConnectionManager()
            .onlinePlayers
            .map {
                MinestomFrameworkPlayer(it)
            }
            .toMutableList()
    }

    override fun getPlayer(id: UUID): FrameworkPlayer? {
        return MinecraftServer.getConnectionManager()
            .getPlayer(id)?.let {
                MinestomFrameworkPlayer(
                    it
                )
            }
    }

    override fun getPlayerCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getDataFolder(): File {
        TODO("Not yet implemented")
    }

}