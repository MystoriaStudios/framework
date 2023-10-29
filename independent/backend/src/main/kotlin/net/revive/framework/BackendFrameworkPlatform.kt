package net.revive.framework

import net.revive.framework.config.JsonConfig
import net.revive.framework.instance.Instance
import net.revive.framework.instance.InstanceService

@JsonConfig("config.json")
class BackendFrameworkPlatform(
    override val id: String = "framework-backend",
    override val groups: MutableList<String> = mutableListOf("Global"),
    val port: Int = 7777
) : IFrameworkPlatform {
    override fun updateInstance(instance: Instance) {
        instance.setMetadata("rawr", "rawr")
    }
}