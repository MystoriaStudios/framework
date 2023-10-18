package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.callback.impl.MaterialCallbackMenu
import net.revive.framework.menu.openMenu
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.utils.ItemStackBuilder
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
                player.openMenu(MaterialCallbackMenu {
                    itemstack = ItemStack(it)
                    player.openMenu(this)
                })
            }
        }
    }

    override fun getTitle(player: Player) = buildComponent(
        "Editing item",
        Tailwind.GRAY_700
    )

    override fun getButtons(player: Player): Map<Int, IButton> {
        return mutableMapOf<Int, IButton>().apply {
            this[4] = object : IButton {
                override fun getMaterial(player: Player) = XMaterial.matchXMaterial(itemstack?.type ?: Material.BARRIER)

                override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                    itemStack = itemstack ?: ItemStack.empty()
                    if (itemStack.isEmpty) {
                        name(buildComponent {
                            text("Change Type") {
                                it.color(Tailwind.RED_600)
                                it.decorate(TextDecoration.BOLD)
                            }
                        })
                        enchantment(Enchantment.VANISHING_CURSE, 1)
                    } else {
                        name(buildComponent {
                            text("Change Type") {
                                it.color(Tailwind.AMBER_400)
                                it.decorate(TextDecoration.BOLD)
                            }
                        })
                    }
                }

                override fun onClick(player: Player, type: ClickType) {
                    player.openMenu(MaterialCallbackMenu {
                        itemstack?.type = it
                        player.openMenu(this@EditItemStackMenu)
                    })
                }
            }
        }
    }
}