package net.revive.framework.entity.hologram

import net.revive.framework.entity.util.DataWatcherEditor
import org.bukkit.entity.Player

object HololgramProtocol {

    fun sendSpawnPackets(player: Player, hologram: AbstractNMSHologram) {
        val hologramText = hologram.processPlaceholders(player, hologram.getText())

        val dataWatcher = DataWatcherEditor {
            register(1, 0.toShort())
            register(2, hologramText)
            register(3, true)
            register(4, false)
            register(5, true)
        }
    }
}