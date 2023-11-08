package net.revive.framework

import net.revive.framework.constants.Deployment
import org.bukkit.Bukkit
import java.io.File

class PaperFrameworkPlatform : IFrameworkPlatform {
    
    override val id: String = Deployment.info.serverId
    override val groups: MutableList<String> = Deployment.info.serverGroups.toMutableList()

}