package net.revive.framework.deathmessage.configuration

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.revive.framework.cache.UUIDCache
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.buildComponent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import java.util.*

interface IDeathMessageConfiguration {
    fun shouldShowDeathMessage(
        viewer: UUID,
        target: UUID,
        killer: UUID?
    ): Boolean

    fun formatPlayerName(
        player: UUID
    ): Component

    fun formatPlayerName(
        player: UUID,
        viewer: UUID
    ) = this.formatPlayerName(player)

    fun hideWeapons(): Boolean = false

    companion object {
        val DEFAULT_CONFIGURATION: IDeathMessageConfiguration = object : IDeathMessageConfiguration {
            override fun shouldShowDeathMessage(
                viewer: UUID,
                target: UUID,
                killer: UUID?
            ) = true

            override fun formatPlayerName(player: UUID) = run {
                val name = UUIDCache.username(player) ?: Bukkit.getPlayer(player)?.name ?: player.toString()

                buildComponent {
                    this.text(name) {
                        it.color(Tailwind.GRAY_100)
                        it.component.hoverEvent(
                            HoverEvent.showEntity(
                                EntityType.PLAYER.key,
                                player,
                                Component.text(name)
                            )
                        )
                    }
                }
            }
        }
    }
}
