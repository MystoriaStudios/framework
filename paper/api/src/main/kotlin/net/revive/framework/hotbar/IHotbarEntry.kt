package net.revive.framework.hotbar

import net.revive.framework.item.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.*

interface IHotbarEntry {

    val uniqueId: UUID

    fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit

    // change to itembuilder
    fun applyTexture(player: Player): ItemStackBuilder.() -> Unit {
        return {

        }
    }

    fun onClick(player: Player, type: ClickType) {}
}