package net.revive.framework.menu.test

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.utils.ItemStackBuilder
import org.bukkit.entity.Player

class TestMenu : AbstractPagedMenu() {

    override val metaData: IMenu.MetaData = IMenu.MetaData()

    override fun getTitle(player: Player): Component {
        return Component.text("Test MENU !!!")
    }

    override fun getAllPagesButtons(player: Player): Map<Int, IButton> {
        val buttons = mutableMapOf<Int, IButton>()

        buttons[15] = TestButton()

        return buttons
    }

    override val maxItemsPerPage: Int
        get() = 9
    override val buttonStartOffset: Int
        get() = 9

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