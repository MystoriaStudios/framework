package net.mystoria.framework.entity.hologram

import net.mystoria.framework.entity.util.DataWatcherEditor
import org.bukkit.entity.Player

object HololgramProtocol {

    fun sendSpawnPackets(player: Player, hologram: AbstractNMSHologram) {
        val hologramText = hologram.processPlaceholders(player, hologram.getText())

        val dataWatcher = DataWatcherEditor {
            set(1, 0.toShort())
            set(2, hologramText)
            set(3, true)
            set(4, false)
            set(5, true)

        }
    }
}