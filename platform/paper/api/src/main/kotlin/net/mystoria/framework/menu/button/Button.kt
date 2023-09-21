package net.mystoria.framework.menu.button

import com.cryptomorin.xseries.XItemStack
import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

interface Button {

    fun getName(player: Player) : Component

    // change to xmaterial
    fun getMaterial(player: Player) : XMaterial
    fun getLore(player: Player) : List<Component>
    fun getButtonItem(player: Player): XItemStack

    fun onClick(player: Player, type: ClickType) {}
    fun onClick(player: Player, type: ClickType, event: InventoryClickEvent) {}

    // change to itembuilder
    fun applyTexture(player: Player, lambda: (XItemStack) -> XItemStack) {
        lambda.invoke(getButtonItem(player))
    }
}