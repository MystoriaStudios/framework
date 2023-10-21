package net.revive.framework.menu.template

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.AbstractFrameworkPlayer
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.buildComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MenuTemplate(
    val id: String,
    val title: Component = buildComponent(id, Tailwind.GRAY_700),
    val buttons: MutableList<Button> = mutableListOf()
) {
    class Button(
        val slot: Int,
        val item: ItemStack
    ) : IButton {
        override fun getMaterial(player: Player) = XMaterial.STONE
        override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
            itemStack = item
        }
    }

    fun build(): IMenu {
        return object : IMenu {
            override val metaData: IMenu.MetaData = IMenu.MetaData()
            override fun getTitle(player: FrameworkPlayer) = title

            override fun getButtons(player: FrameworkPlayer) = mutableMapOf<Int, IButton>().apply {
                buttons.forEach {
                    this[it.slot] = it
                }
            }
        }
    }
}