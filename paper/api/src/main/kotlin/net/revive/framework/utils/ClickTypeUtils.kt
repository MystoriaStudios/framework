package net.revive.framework.utils

import net.revive.framework.component.ClickType

fun ClickType.toBukkit(): org.bukkit.event.inventory.ClickType = org.bukkit.event.inventory.ClickType.valueOf(this.name)
fun org.bukkit.event.inventory.ClickType.toFramework(): ClickType = ClickType.valueOf(this.name)