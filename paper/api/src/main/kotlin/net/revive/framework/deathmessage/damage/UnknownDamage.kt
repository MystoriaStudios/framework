package net.revive.framework.deathmessage.damage

import java.util.*
import org.bukkit.ChatColor as CC

class UnknownDamage(
    damaged: UUID,
    damage: Double
) : AbstractDamage(
    damaged,
    damage
) {
    override fun getDeathMessage(player: UUID) = wrapName(damaged, player) + CC.YELLOW + " died."
}
