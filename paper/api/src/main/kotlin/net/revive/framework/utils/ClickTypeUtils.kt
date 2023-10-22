package net.revive.framework.utils

import net.revive.framework.component.ClickType

val ClickType.pdc: org.bukkit.event.inventory.ClickType
    get() = org.bukkit.event.inventory.ClickType.valueOf(this.name)

val org.bukkit.event.inventory.ClickType.pvc: ClickType
    get() = ClickType.valueOf(this.name)