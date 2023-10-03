package net.mystoria.framework.deathmessage.damage

import net.mystoria.framework.deathmessage.DeathMessageService
import java.util.*

abstract class AbstractDamage(
    val damaged: UUID,
    val damage: Double,
    val time: Long = System.currentTimeMillis()
) {
    abstract fun getDeathMessage(player: UUID): String

    val timeAgoMillis: Long get() = System.currentTimeMillis() - time

    companion object {
        fun wrapName(player: UUID, wrapFor: UUID): String {
            val configuration = DeathMessageService.configuration
            return configuration.formatPlayerName(player, wrapFor)
        }

        fun wrapName(player: UUID): String {
            val configuration = DeathMessageService.configuration
            return configuration.formatPlayerName(player)
        }
    }
}
