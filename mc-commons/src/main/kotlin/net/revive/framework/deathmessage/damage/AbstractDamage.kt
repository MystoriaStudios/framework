package net.revive.framework.deathmessage.damage

import net.kyori.adventure.text.Component
import net.revive.framework.deathmessage.DeathMessageService
import java.util.*

abstract class AbstractDamage(
    val damaged: UUID,
    val damage: Double,
    val time: Long = System.currentTimeMillis()
) {
    abstract fun getDeathMessage(player: UUID): Component

    val timeAgoMillis: Long get() = System.currentTimeMillis() - time

    companion object {
        fun wrapName(player: UUID, wrapFor: UUID): Component {
            val configuration = DeathMessageService.configuration
            return configuration.formatPlayerName(player, wrapFor)
        }

        fun wrapName(player: UUID): Component {
            val configuration = DeathMessageService.configuration
            return configuration.formatPlayerName(player)
        }
    }
}
