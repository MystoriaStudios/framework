package net.revive.framework

import net.revive.framework.config.NodeConfig
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.sender.PaperFrameworkPlayer
import net.revive.framework.server.IMinecraftPlatform
import org.bukkit.Bukkit
import java.io.File
import java.util.*

object PaperMinecraftPlatform : IMinecraftPlatform {

    @Inject
    lateinit var nodeConfig: NodeConfig

    override fun getNodeIP() = nodeConfig.nodeIP

    override fun getNodePort() = nodeConfig.nodePort

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

    override fun getPlayerCount(): Int {
        return Bukkit.getOnlinePlayers().size
    }

    override fun getDataFolder(): File {
        return Bukkit.getPluginManager()
            .getPlugin("Framework")!!
            .dataFolder
    }
}