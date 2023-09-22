package net.mystoria.framework.scoreboard

import fr.mrmicky.fastboard.adventure.FastBoard
import me.lucko.helper.Events
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import net.mystoria.framework.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

@Service
object ScoreboardService {

    var handle: MutableMap<UUID, FastBoard> = mutableMapOf()
    var playerScoreboards: MutableMap<UUID, IScoreboard> = mutableMapOf()

    var primaryScoreboard: IScoreboard? = null

    @Configure
    fun configure() {

    }

    fun updatePrimaryProvider(scoreboard: IScoreboard) {
        this.primaryScoreboard = scoreboard
        Events.subscribe(PlayerJoinEvent::class.java).handler {
            val player = it.player
            val board = FastBoard(player)
            handle[player.uniqueId] = board
            refresh(player)
        }

        Events.subscribe(PlayerQuitEvent::class.java).handler {
            val player = it.player
            val board = handle[player.uniqueId] ?: return@handler
            board.delete()
        }

        Tasks.asyncTimer(10L, 10L) {
            Bukkit.getServer().onlinePlayers.filter { handle.containsKey(it.uniqueId) }.forEach {
                refresh(it)
            }
        }
    }

    fun refresh(player: Player) {
        val board = handle[player.uniqueId].apply {
            if (this == null) handle[player.uniqueId] = FastBoard(player)
            handle[player.uniqueId]
        } ?: return

        val scoreboard = playerScoreboards.getOrDefault(player.uniqueId, primaryScoreboard) ?: return

        board.updateTitle(scoreboard.getTitle(player))
        board.updateLines(scoreboard.getScores(player))
    }

    fun apply(player: Player, scoreboard: IScoreboard) {
        playerScoreboards[player.uniqueId] = scoreboard
        refresh(player)
    }
}