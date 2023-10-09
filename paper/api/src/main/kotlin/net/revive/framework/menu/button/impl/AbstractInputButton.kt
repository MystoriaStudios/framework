package net.revive.framework.menu.button.impl

import net.revive.framework.menu.button.IButton
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

abstract class AbstractInputButton : IButton {
    override fun onClick(player: Player, type: ClickType) {
        builder(player).open(player)
    }

    abstract fun builder(player: Player) : AnvilGUI.Builder
}