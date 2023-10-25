package net.revive.framework.menu.callback.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.component.ClickType
import net.revive.framework.constants.Tailwind
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.callback.AbstractCallbackPagedMenu
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.toMinecraftKey
import java.lang.reflect.Field

class ColorCallbackMenu(
    override var onCallback: (String) -> Unit
) : AbstractCallbackPagedMenu<String>() {
    override fun getTitle(player: FrameworkPlayer) = buildComponent(
        "Selecting color",
        Tailwind.GRAY_700
    )

    override fun getAllPagesButtons(player: FrameworkPlayer): Map<Int, IButton> {
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
        override fun getMaterial(player: FrameworkPlayer) = XMaterial.LEATHER_CHESTPLATE.toMinecraftKey()

        override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
            name(
                buildComponent(
                    field.name,
                    field.get(Tailwind).toString()
                )
            )

            TextColor.fromHexString(field.get(Tailwind).toString())?.let { color(it) }
        }

        override fun onClick(player: FrameworkPlayer, type: ClickType) {
            onCallback.invoke(field.get(Tailwind).toString())
        }
    }
}