package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.openMenu
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.storage.storable.IStorable
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.Strings
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.buildComponent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * @param itemstack The itemstack to edit or if null it will force user to choose the  material thus initiating it
 */
class EditItemStackMenu(private var itemstack: ItemStack? = null) : IMenu {

    override val metaData: IMenu.MetaData = IMenu.MetaData()
    override val cancelClicks: Boolean = true

    override fun onOpen(player: Player) {
        if (itemstack == null) {
            Tasks.delayed(1L) {
                player.openMenu(EditMaterialMenu(this))
            }
        }
    }

    override fun getTitle(player: Player) = buildComponent(
        "Editing item",
        Tailwind.GRAY_700
    )

    override fun getButtons(player: Player): Map<Int, IButton> {
        return mutableMapOf<Int, IButton>().apply {
            this[3] = object : IButton {
                override fun getMaterial(player: Player) = XMaterial.matchXMaterial(itemstack ?: ItemStack.empty())

                override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                    itemStack = itemstack ?: ItemStack.empty()
                    if (itemStack.isEmpty) {
                        type(Material.BARRIER)
                        name(buildComponent {
                            text("Change Item") {
                                it.color(Tailwind.RED_600)
                                it.decorate(TextDecoration.BOLD)
                            }
                        })
                        enchantment(Enchantment.VANISHING_CURSE, 1)
                    }
                }

                override fun onClick(player: Player, type: ClickType) {
                    player.openMenu(EditMaterialMenu(this@EditItemStackMenu))
                }
            }
        }
    }

    inner class EditMaterialMenu(val parentMenu: EditItemStackMenu) : AbstractPagedMenu() {

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
                if (parentMenu.itemstack == null) {
                    parentMenu.itemstack = ItemStack(this.type)
                } else {
                    parentMenu.itemstack!!.type = this.type
                }

                player.openMenu(parentMenu)
            }
        }
    }
}