package net.mystoria.framework.visibility

import org.bukkit.entity.Player

interface IVisibilityProvider {
    fun handle(viewer: Player, target: Player): VisibilityResult
}