package net.revive.framework.deathmessage.configuration

import net.kyori.adventure.text.Component
import net.revive.framework.cache.UUIDCache
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.buildComponent
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
                val name = UUIDCache.username(player) ?: player.toString()

                buildComponent {
                    this.text(name) {
                        it.color(Tailwind.GRAY_100)
                    }
                }
            }
        }
    }
}
