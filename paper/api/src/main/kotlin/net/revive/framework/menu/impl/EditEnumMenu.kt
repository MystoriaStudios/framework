package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.annotation.detail.Export
import net.revive.framework.component.impl.CategoryComponent
import net.revive.framework.component.impl.ControlComponent
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.buildComponent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EditEnumMenu<T : Enum<*>>(val enums: Array<T>, val save: (T) -> Unit) : AbstractPagedMenu() {

    override val metaData: IMenu.MetaData = IMenu.MetaData()
    override val buttonStartOffset = 9
    override val maxItemsPerPage = 21

    /**
     * Retrieves a map of button positions to button identifiers for all pages of the menu.
     *
     * @param player The player for whom the buttons are generated.
     * @return A map of button positions (slot numbers) to button identifiers (IButton) for all pages.
     */
    override fun getAllPagesButtons(player: Player) = mutableMapOf<Int, IButton>().apply {
        enums.forEach {
            this[this.size] = object : IButton {
                override fun getMaterial(player: Player) = XMaterial.WRITABLE_BOOK
                override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                    name(buildComponent {
                        text(it.name) {
                            it.color(Tailwind.AMBER_400)
                            it.decorate(TextDecoration.BOLD)
                        }
                    })

                    lore(
                        mutableListOf(
                            CategoryComponent("Enum State").build(),
                            Component.empty(),
                        ).apply {
                            it::class.java.declaredFields.filter {
                                it.isAnnotationPresent(Export::class.java)
                            }.forEach {
                                val annotation = it.getAnnotation(Export::class.java)
                                val name = annotation.name.ifEmpty {
                                    it.name
                                }

                                add(buildComponent {
                                    text(" • ", Tailwind.GRAY_700)
                                    text("$name: ", Tailwind.AMBER_400)
                                    text(it.get(it).toString(), Tailwind.TEAL_700)
                                })
                            }
                            addAll(
                                listOf(
                                    Component.empty(),
                                    ControlComponent(ClickType.LEFT, "set the enum to this").build()
                                )
                            )
                        })
                }

                override fun onClick(player: Player, type: ClickType) {
                    save.invoke(it)
                }
            }
        }
    }

    override fun getTitle(player: Player) = buildComponent(
        "Browsing ${enums.first()::class.java.simpleName}", Tailwind.GRAY_700
    )
}