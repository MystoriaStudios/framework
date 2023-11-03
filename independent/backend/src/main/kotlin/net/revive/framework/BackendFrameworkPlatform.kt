package net.revive.framework

import net.revive.framework.config.JsonConfig

@JsonConfig("config.json")
class BackendFrameworkPlatform(
    override val id: String = "framework-backend",
    override val groups: MutableList<String> = mutableListOf("Global"),
    val port: Int = 7777
) : IFrameworkPlatform