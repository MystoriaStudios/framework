package net.revive.framework

import net.revive.framework.config.JsonConfig
import net.revive.framework.node.Node
import java.util.*

@JsonConfig("platform.json")
class FrameworkNodePlatform(
    override val id: String = "test-node",
    override val groups: MutableList<String> = mutableListOf("NA"),
    val publicAddress: String = "100.110.183.133",
    val hostAddress: String = "0.0.0.0",
    val port: Int = 8080,
    val gRPCPort: Int = 8070,
    var api_key: String = "org_2XQOxNamtty4AnwFGyepkxOdq7F",
    val identifier: UUID = UUID.randomUUID(),
    var runningState: Node.State = Node.State.SETUP
) : IFrameworkPlatform