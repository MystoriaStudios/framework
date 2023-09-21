package net.mystoria.framework.menu

import org.bukkit.entity.Player

interface Menu {

    fun getTitle(player: Player) : String
    fun getInventoryTexture(player: Player) : String = getTitle(player)

    fun getButtons(player: Player) : Map<Int, String> // change to bytton
}
