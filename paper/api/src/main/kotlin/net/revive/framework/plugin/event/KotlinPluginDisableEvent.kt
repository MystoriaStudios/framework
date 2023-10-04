package net.revive.framework.plugin.event

import net.revive.framework.event.SimpleEvent
import net.revive.framework.plugin.ExtendedKotlinPlugin

class KotlinPluginDisableEvent(val plugin: ExtendedKotlinPlugin) : SimpleEvent() {
}