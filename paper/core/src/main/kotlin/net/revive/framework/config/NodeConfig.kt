package net.revive.framework.config

@JsonConfig("pod.json")
class NodeConfig(
    var nodeIP: String = "0.0.0.0",
    var nodePort: Int = 8070
)