package net.revive.framework.menu.callback.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.callback.AbstractCallbackPagedMenu
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.buildComponent
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.lang.reflect.Field

class ColorCallbackMenu(
    override var onCallback: (String) -> Unit
) : AbstractCallbackPagedMenu<String>() {
    override fun getTitle(player: Player) = buildComponent(
        "Selecting color",
        Tailwind.GRAY_700
    )

    override fun getAllPagesButtons(player: Player): Map<Int, IButton> {
        var i = 0;
        return Tailwind::class.java.declaredFields
            .filter { it.name != "INSTANCE" }
            .map {
                return@map ColorButton(it)
            }.associateBy {
                i++
                i
            }
    }

    override val maxItemsPerPage: Int = 27
    override val buttonStartOffset: Int = 9
    override val metaData: IMenu.MetaData = IMenu.MetaData()

    inner class ColorButton(val field: Field) : IButton {
        override fun getMaterial(player: Player) = XMaterial.LEATHER_CHESTPLATE

        override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
            name(buildComponent(
                    field.name,
                    field.get(Tailwind).toString()
            ))

            color(Color.fromRGB(TextColor.fromHexString(field.get(Tailwind).toString())?.value() ?: 0))
        }

        override fun onClick(player: Player, type: ClickType) {
            onCallback.invoke(field.get(Tailwind).toString())
        }
    }
}