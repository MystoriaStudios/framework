package net.mystoria.framework.menu

import org.bukkit.entity.Player

interface Menu {

    fun getTitle(player: Player): String
}
