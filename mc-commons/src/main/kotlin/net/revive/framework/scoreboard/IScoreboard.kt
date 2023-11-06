package net.revive.framework.scoreboard

import net.kyori.adventure.text.Component
import net.revive.framework.sender.FrameworkPlayer

interface IScoreboard {

    fun getTitle(player: FrameworkPlayer): Component
    fun getScores(player: FrameworkPlayer): List<Component>
    fun shouldDisplay(player: FrameworkPlayer) = true
}