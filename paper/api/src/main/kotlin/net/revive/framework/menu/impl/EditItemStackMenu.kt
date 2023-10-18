package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.button.impl.AbstractInputButton
import net.revive.framework.menu.callback.impl.MaterialCallbackMenu
import net.revive.framework.menu.openMenu
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.buildComponent
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
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
        return mapOf<Int, IButton>(
            4 to object : IButton {
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
            },

            5 to object : AbstractInputButton() {
                override fun builder(player: Player): AnvilGUI.Builder = AnvilGUI.Builder()
                .plugin(Bukkit.getPluginManager().getPlugin("Framework"))
                .text("Enter the new item name")
                .onClick { _, event ->
                    itemstack?.editMeta {
                        it.displayName(MiniMessage.miniMessage().deserialize(event.text))
                    }
                    player.openMenu(this@EditItemStackMenu)
                    return@onClick listOf(AnvilGUI.ResponseAction.close())
                }

                override fun getMaterial(player: Player): XMaterial = XMaterial.OAK_SIGN

                override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                    name(buildComponent {
                        text("Change Name") {
                            it.color(Tailwind.AMBER_400)
                            it.decorate(TextDecoration.BOLD)
                        }
                    })
                }
            }
        )
    }
}