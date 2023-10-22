package net.revive.framework.menu.button.impl

import net.revive.framework.component.ClickType
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.sender.FrameworkPlayer
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player

abstract class AbstractInputButton : IButton {
    override fun onClick(player: FrameworkPlayer, type: ClickType) {
        player as AbstractFrameworkPlayer<Player>
        builder(player).open(player.player)
    }

    abstract fun builder(player: FrameworkPlayer): AnvilGUI.Builder
}