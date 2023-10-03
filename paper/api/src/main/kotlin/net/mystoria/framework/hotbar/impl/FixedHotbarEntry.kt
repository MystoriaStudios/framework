package net.mystoria.framework.hotbar.impl

import net.mystoria.framework.hotbar.IHotbarEntry
import net.mystoria.framework.utils.ItemStackBuilder
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