package net.revive.framework.plugin.event

import net.revive.framework.event.SimpleEvent
import net.revive.framework.plugin.ExtendedKotlinPlugin

class KotlinPluginEnableEvent(val plugin: ExtendedKotlinPlugin) : SimpleEvent() {
}