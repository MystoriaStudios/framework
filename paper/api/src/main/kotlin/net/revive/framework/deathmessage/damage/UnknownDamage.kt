package net.revive.framework.deathmessage.damage

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.buildComponent
import java.util.*
import org.bukkit.ChatColor as CC

class UnknownDamage(
    damaged: UUID,
    damage: Double
) : AbstractDamage(
    damaged,
    damage
) {
    override fun getDeathMessage(player: UUID) = buildComponent(wrapName(damaged, player)) {
        text(" died.", Tailwind.ORANGE_400)
    }
}
