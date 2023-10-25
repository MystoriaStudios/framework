package net.revive.framework

import net.revive.framework.annotation.inject.AutoBind
import net.revive.framework.instance.Instance

@AutoBind
object MinestomFrameworkPlatform : IFrameworkPlatform {

    override val id: String = "unknown"
    override val groups: MutableList<String> = mutableListOf("unknown")

    override fun updateInstance(instance: Instance) {
        // ADD ANY METRICS HEREE.
    }
}