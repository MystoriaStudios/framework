package net.revive.framework.menu.callback.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.revive.framework.component.ClickType
import net.revive.framework.constants.Tailwind
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.key.MinecraftKey
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.callback.AbstractCallbackPagedMenu
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.toMinecraftKey
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

class MaterialCallbackMenu(
    override var onCallback: (Material) -> Unit
) : AbstractCallbackPagedMenu<Material>() {
    override fun getTitle(player: FrameworkPlayer) = buildComponent(
        "Selecting item type",
        Tailwind.GRAY_700
    )

    override fun getAllPagesButtons(player: FrameworkPlayer): Map<Int, IButton> {
        var i = 0;
        return Material
            .entries
            .filter { !it.isAir && it.isItem }
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
        override fun getMaterial(player: FrameworkPlayer) = XMaterial.matchXMaterial(type).toMinecraftKey()

        override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
            name(
                buildComponent(
                    type.name.lowercase().split("_").joinToString(" ") {
                        it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }, Tailwind.GREEN_500
                )
            )

            lore(
                Component.empty(),
                buildComponent("Click to change select this material type", Tailwind.GRAY_500)
            )
        }

        override fun onClick(player: FrameworkPlayer, type: ClickType) {
            onCallback.invoke(this.type)
        }
    }
}