package net.revive.framework

import net.revive.framework.config.JsonConfig
import net.revive.framework.instance.Instance
import net.revive.framework.instance.InstanceService

@JsonConfig("platform.json")
class IndependentFrameworkPlatform(
    override val id: String = "na-node-country",
    override val groups: MutableList<String> = mutableListOf("NA"),
    val port: Int = 8080
) : IFrameworkPlatform {
    override fun updateInstance(instance: Instance) {
        instance.setMetadata("rawr", "rawr")
    }
}