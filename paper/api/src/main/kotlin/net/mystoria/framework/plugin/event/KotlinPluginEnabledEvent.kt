package net.mystoria.framework.plugin.event

import net.mystoria.framework.event.SimpleEvent
import net.mystoria.framework.plugin.ExtendedKotlinPlugin

class KotlinPluginEnabledEvent(val plugin: ExtendedKotlinPlugin) : SimpleEvent() {
}