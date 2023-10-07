package net.revive.framework.visibility

import net.revive.framework.visibility.override.IOverrideHandler
import net.revive.framework.visibility.override.OverrideResult
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface IVisibilityHandler {
    fun registerProvider(identifier: String, handler: IVisibilityProvider)

    fun registerOverride(identifier: String, handler: IOverrideHandler)

    fun update(player: Player)

    @Deprecated("")
    fun updateAllTo(viewer: Player)

    @Deprecated("")
    fun updateToAll(target: Player)

    fun treatAsOnline(target: Player, viewer: Player): Boolean

    fun shouldSee(target: Player, viewer: Player): Boolean

    fun getDebugInfo(target: Player, viewer: Player): List<String>
}