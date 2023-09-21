package net.mystoria.framework.menu.button

import com.cryptomorin.xseries.XMaterial
import net.mystoria.framework.utils.ItemBuilder
import net.mystoria.framework.utils.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

interface IButton {
    fun getMaterial(player: Player) : XMaterial
    fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit

    fun onClick(player: Player, type: ClickType) {}
    fun onClick(player: Player, type: ClickType, event: InventoryClickEvent) {}

    // change to itembuilder
    fun applyTexture(player: Player): ItemStackBuilder.() -> Unit {
        return {

        }
    }
}