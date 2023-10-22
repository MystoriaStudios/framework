package net.revive.framework

import net.minestom.server.MinecraftServer
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.MinestomFrameworkPlayer
import net.revive.framework.server.IMinecraftPlatform
import java.util.*

object MinestomMinecraftPlatform : IMinecraftPlatform {
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

}