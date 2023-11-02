package net.revive.framework

import net.revive.framework.config.JsonConfig
import net.revive.framework.instance.Instance
import java.util.*

@JsonConfig("platform.json")
class FrameworkNodePlatform(
    override val id: String = "test-node",
    override val groups: MutableList<String> = mutableListOf("NA"),
    val port: Int = 8080,
    val gRPCPort: Int = 8070,
    val api_key: String = "org_2XQOxNamtty4AnwFGyepkxOdq7F",
    val identifier: UUID = UUID.randomUUID()
) : IFrameworkPlatform {
    override fun updateInstance(instance: Instance) {
        instance.setMetadata("rawr", "rawr")
    }
}