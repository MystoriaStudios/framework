package net.revive.framework.menu.callback.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.callback.AbstractCallbackPagedMenu
import net.revive.framework.menu.callback.ICallbackMenu
import net.revive.framework.menu.openMenu
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.buildComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class MaterialCallbackMenu(
    override var onCallback: (Material) -> Unit
) : AbstractCallbackPagedMenu<Material>() {
    override fun getTitle(player: Player) = buildComponent(
        "Selecting item type",
        Tailwind.GRAY_700
    )

    override fun getAllPagesButtons(player: Player): Map<Int, IButton> {
        var i = 0;
        return Material
            .entries
            .filter { !it.isAir }
            .map {
                return@map MaterialButton(it)
            }.associateBy {
                i++
                i
            }
    }

    override val maxItemsPerPage: Int = 27
    override val buttonStartOffset: Int = 9
    override val metaData: IMenu.MetaData = IMenu.MetaData()

    inner class MaterialButton(val type: Material) : IButton {
        override fun getMaterial(player: Player) = XMaterial.matchXMaterial(type)

        override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
            name(
                buildComponent(
                    type.name.lowercase().capitalize(), Tailwind.GREEN_500
                )
            )

            lore(
                Component.empty(),
                buildComponent("Click to change the item's type", Tailwind.GRAY_500),
                buildComponent("to this.", Tailwind.GRAY_500)
            )
        }

        override fun onClick(player: Player, type: ClickType) {
            onCallback.invoke(this.type)
        }
    }
}