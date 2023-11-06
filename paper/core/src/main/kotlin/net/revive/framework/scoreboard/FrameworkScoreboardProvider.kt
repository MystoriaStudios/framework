package net.revive.framework.scoreboard

import net.revive.framework.annotation.Scoreboard
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.scoreboard.config.ScoreboardConfig
import net.revive.framework.sender.FrameworkPlayer
import org.bukkit.entity.Player

@Scoreboard
object FrameworkScoreboardProvider : IScoreboard {

    @Inject
    lateinit var config: ScoreboardConfig

    override fun getTitle(player: FrameworkPlayer) = config.template.title
    override fun getScores(player: FrameworkPlayer) = config.template.lines
    override fun shouldDisplay(player: FrameworkPlayer) = config.enabled
}