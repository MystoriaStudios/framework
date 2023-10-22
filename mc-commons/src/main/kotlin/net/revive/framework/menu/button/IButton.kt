package net.revive.framework.menu.button

import net.kyori.adventure.key.Key
import net.revive.framework.component.ClickType
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.sender.FrameworkPlayer

interface IButton {
    fun getMaterial(player: FrameworkPlayer): Key
    fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit

    fun onClick(player: FrameworkPlayer, type: ClickType) {}

    fun applyTexture(player: FrameworkPlayer): ItemStackBuilder.() -> Unit {
        return {

        }
    }
}