package net.mystoria.framework.visibility.override

import org.bukkit.entity.Player

interface IOverrideHandler {
    fun handle(viewer: Player, target: Player): OverrideResult
}