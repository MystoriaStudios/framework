package net.revive.framework

import net.revive.framework.annotation.inject.AutoBind

@AutoBind
object MinestomFrameworkPlatform : IFrameworkPlatform {

    override val id: String = "unknown"
    override val groups: MutableList<String> = mutableListOf("unknown")
}