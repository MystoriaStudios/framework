package net.mystoria.framework.menu.button

import com.cryptomorin.xseries.XMaterial
import net.mystoria.framework.utils.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

interface IButton {
    fun getMaterial(player: Player) : XMaterial
    fun getButtonItem(player: Player): ItemStack

    fun onClick(player: Player, type: ClickType) {}
    fun onClick(player: Player, type: ClickType, event: InventoryClickEvent) {}

    // change to itembuilder
    fun applyTexture(player: Player, itemStackBuilder: ItemBuilder, lambda: (ItemBuilder) -> ItemBuilder) {
        //lambda.invoke(getButtonItem(player))
    }
}