package net.mystoria.framework.menu.test

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.mystoria.framework.menu.IMenu
import net.mystoria.framework.menu.button.IButton
import net.mystoria.framework.utils.ItemStackBuilder
import org.bukkit.entity.Player

class TestMenu : IMenu {
    override val metaData: IMenu.MetaData = IMenu.MetaData()
    override fun getTitle(player: Player): Component {
        return Component.text("Test MENU !!!")
    }

    override fun getButtons(player: Player): Map<Int, IButton> {
        val buttons = mutableMapOf<Int, IButton>()

        buttons[15] = TestButton()

        return buttons
    }

    override fun size(buttons: Map<Int, IButton>): Int {
        return 27
    }

    inner class TestButton : IButton {
        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.PAPER
        }

        override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit {
            return {
                name(Component.text("Test Test "))
            }
        }

    }
}