package net.revive.framework.config

@JsonConfig("pod.json")
class NodeConfig(
    var nodeIP: String = "127.0.0.1",
    var nodePort: Int = 8070
)