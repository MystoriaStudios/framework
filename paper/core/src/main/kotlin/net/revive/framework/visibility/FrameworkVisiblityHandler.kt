@file:Suppress("DEPRECATION")

package net.revive.framework.visibility

import me.lucko.helper.Events
import net.revive.framework.flavor.service.Configure
import net.revive.framework.visibility.override.IOverrideHandler
import net.revive.framework.visibility.override.OverrideResult
import org.apache.commons.lang3.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerJoinEvent

class FrameworkVisiblityHandler : IVisibilityHandler {

    lateinit var providers: MutableMap<String, IVisibilityProvider>
    lateinit var overrides: MutableMap<String, IOverrideHandler>

    @Configure
    fun configure() {
        Events.subscribe(PlayerJoinEvent::class.java).handler { update(it.player) }

        Events.subscribe(PlayerChatTabCompleteEvent::class.java).handler { event ->
            val token = event.lastToken
            val completions = event.tabCompletions
            completions.clear()
            for (target in Bukkit.getOnlinePlayers()) {
                if (!treatAsOnline(target, event.player)) continue
                if (!StringUtils.startsWithIgnoreCase(target.name, token)) continue

                completions.add(target.name)
            }
        }
    }

    override fun registerProvider(identifier: String, handler: IVisibilityProvider) {
        providers[identifier] = handler
    }

    override fun registerOverride(identifier: String, handler: IOverrideHandler) {
        overrides[identifier] = handler
    }

    override fun update(player: Player) {
        if (providers.isEmpty() && overrides.isEmpty()) {
            return
        }
        updateAllTo(player)
        updateToAll(player)
    }

    @Deprecated("")
    override fun updateAllTo(viewer: Player) {
        for (target in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target)
            } else {
                viewer.showPlayer(target)
            }
        }
    }

    @Deprecated("")
    override fun updateToAll(target: Player) {
        for (viewer in Bukkit.getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) {
                viewer.hidePlayer(target)
            } else {
                viewer.showPlayer(target)
            }
        }
    }

    override fun treatAsOnline(target: Player, viewer: Player): Boolean {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("framework.staff")
    }

    override fun shouldSee(target: Player, viewer: Player): Boolean {
        for (handler in overrides.values) {
            if (handler.handle(target, viewer) === OverrideResult.SHOW) {
                return true
            }
        }
        for (handler2 in providers.values) {
            if (handler2.handle(target, viewer) === VisibilityResult.HIDE) {
                return false
            }
        }
        return true
    }
}