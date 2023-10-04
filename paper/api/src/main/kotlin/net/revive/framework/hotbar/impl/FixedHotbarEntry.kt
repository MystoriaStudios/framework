package net.revive.framework.hotbar.impl

import net.revive.framework.hotbar.IHotbarEntry
import net.revive.framework.utils.ItemStackBuilder
import org.bukkit.entity.Player
import java.util.*

class FixedHotbarEntry(
    val builder: ItemStackBuilder.() -> Unit
) : IHotbarEntry {

    override val uniqueId: UUID = UUID.randomUUID()
    override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit {
        return builder
    }
}