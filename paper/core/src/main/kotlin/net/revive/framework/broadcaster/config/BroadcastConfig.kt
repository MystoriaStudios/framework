package net.revive.framework.broadcaster.config

import net.kyori.adventure.text.Component
import net.revive.framework.config.JsonConfig

@JsonConfig("broadcasts.json")
class BroadcastConfig(
    var enabled: Boolean,
    var messages: MutableList<MutableList<Component>> = mutableListOf(),
    var delay: Int = 60
)