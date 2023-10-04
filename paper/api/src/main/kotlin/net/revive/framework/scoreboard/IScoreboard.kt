package net.revive.framework.scoreboard

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface IScoreboard {

    fun getTitle(player: Player) : Component
    fun getScores(player: Player) : List<Component>
    fun shouldDisplay(player: Player) = true
}