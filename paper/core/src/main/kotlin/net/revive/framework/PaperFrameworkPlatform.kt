package net.revive.framework

import net.revive.framework.instance.Instance
import org.bukkit.Bukkit

object PaperFrameworkPlatform : IFrameworkPlatform {

    override val id: String = "unknown"
    override val groups: MutableList<String> = mutableListOf("unknown")

    override fun updateInstance(instance: Instance) {
        instance.metaData.apply {
            this["tps"] = Bukkit.getServer().tps.contentToString()
        }
    }
}