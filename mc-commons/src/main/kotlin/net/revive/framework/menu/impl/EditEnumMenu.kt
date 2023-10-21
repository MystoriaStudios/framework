package net.revive.framework.menu.impl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.annotation.detail.Export
import net.revive.framework.component.ClickType
import net.revive.framework.component.impl.CategoryComponent
import net.revive.framework.component.impl.ControlComponent
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.buildComponent

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
    override fun getAllPagesButtons(player: FrameworkPlayer) = mutableMapOf<Int, IButton>().apply {
        enums.forEach {
            this[this.size] = object : IButton {
                override fun getMaterial(player: FrameworkPlayer) = XMaterial.WRITABLE_BOOK
                override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
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
                                runCatching {
                                    val annotation = it.getAnnotation(Export::class.java)
                                    val name = annotation.name.ifEmpty {
                                        it.name
                                    }

                                    it.isAccessible = true

                                    add(buildComponent {
                                        text(" • ", Tailwind.GRAY_700)
                                        text("$name: ", Tailwind.AMBER_400)
                                        text(it.get(it).toString(), Tailwind.TEAL_700)
                                    })
                                }
                            }
                            addAll(
                                listOf(
                                    Component.empty(),
                                    ControlComponent(ClickType.LEFT.toFramework(), "set the enum to this").build()
                                )
                            )
                        })
                }

                override fun onClick(player: FrameworkPlayer, type: ClickType) {
                    save.invoke(it)
                }
            }
        }
    }

    override fun getTitle(player: FrameworkPlayer) = buildComponent(
        "Browsing ${enums.first()::class.java.simpleName}", Tailwind.GRAY_700
    )
}