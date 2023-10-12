package net.revive.framework.broadcaster.config

import net.kyori.adventure.text.Component
import net.revive.framework.config.JsonConfig
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.buildComponent

@JsonConfig("broadcasts.json")
class BroadcastConfig(
    var enabled: Boolean = true,
    var messages: MutableList<MutableList<Component>> = mutableListOf(mutableListOf(buildComponent("test", Tailwind.RED_400))),
    var delay: Int = 60
)