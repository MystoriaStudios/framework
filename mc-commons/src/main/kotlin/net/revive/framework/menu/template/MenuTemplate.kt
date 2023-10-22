package net.revive.framework.menu.template

import net.kyori.adventure.text.Component
import net.revive.framework.constants.Tailwind
import net.revive.framework.item.FrameworkItemStack
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.key.MinecraftKey
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.buildComponent
class MenuTemplate(
    val id: String,
    val title: Component = buildComponent(id, Tailwind.GRAY_700),
    val buttons: MutableList<Button> = mutableListOf()
) {
    class Button(
        val slot: Int,
        val item: FrameworkItemStack
    ) : IButton {
        override fun getMaterial(player: FrameworkPlayer) = MinecraftKey("stone")
        override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
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