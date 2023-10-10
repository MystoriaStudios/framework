package net.revive.framework.scoreboard

import net.revive.framework.annotation.Scoreboard
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.scoreboard.config.ScoreboardConfig
import org.bukkit.entity.Player

@Scoreboard
object FrameworkScoreboardProvider : IScoreboard {

    @Inject lateinit var config: ScoreboardConfig

    override fun getTitle(player: Player) = config.template.title
    override fun getScores(player: Player) = config.template.lines
    override fun shouldDisplay(player: Player) = config.enabled
}