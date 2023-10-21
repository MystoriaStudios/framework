package net.revive.framework.menu.button

import net.revive.framework.component.ClickType
import net.revive.framework.sender.FrameworkPlayer

interface IButton {
    fun getMaterial(player: FrameworkPlayer): XMaterial
    fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit

    fun onClick(player: FrameworkPlayer, type: ClickType) {}
    fun onClick(player: FrameworkPlayer, type: ClickType, event: InventoryClickEvent) {}

    // change to itembuilder
    fun applyTexture(player: FrameworkPlayer): ItemStackBuilder.() -> Unit {
        return {

        }
    }
}