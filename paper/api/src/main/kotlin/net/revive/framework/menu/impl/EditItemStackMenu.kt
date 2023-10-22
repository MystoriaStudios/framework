package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.revive.framework.component.ClickType
import net.revive.framework.constants.Tailwind
import net.revive.framework.item.FrameworkItemFlag
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.item.PaperFrameworkItemStack
import net.revive.framework.item.buildItem
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.button.impl.AbstractInputButton
import net.revive.framework.menu.callback.impl.MaterialCallbackMenu
import net.revive.framework.menu.openMenu
import net.revive.framework.sender.FrameworkPlayer
import net.revive.framework.utils.Tasks
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.toMinecraftKey
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @param reference The itemstack to edit or if null it will force user to choose the  material thus initiating it
 */
class EditItemStackMenu(private var reference: ItemStack = ItemStack.empty()) : IMenu {

    override val metaData: IMenu.MetaData = IMenu.MetaData()
    override val cancelClicks: Boolean = true

    override fun onOpen(player: FrameworkPlayer) {
        if (reference.isEmpty) {
            Tasks.delayed(1L) {
                player.openMenu(MaterialCallbackMenu {
                    reference = ItemStack(it)
                    player.openMenu(this)
                })
            }
        }
    }

    override fun getTitle(player: FrameworkPlayer) = buildComponent(
        "Editing item",
        Tailwind.GRAY_700
    )

    override fun getButtons(player: FrameworkPlayer): Map<Int, IButton> {
        return mapOf(
            0 to object : IButton {
                override fun getMaterial(player: FrameworkPlayer) = XMaterial.matchXMaterial(reference.type).toMinecraftKey()

                override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
                    itemStack = PaperFrameworkItemStack(reference.clone())

                    if ((itemStack.item as ItemStack).isEmpty) {
                        name(buildComponent {
                            text("Change Type") {
                                it.color(Tailwind.RED_600)
                                it.decorate(TextDecoration.BOLD)
                            }
                        })
                        //TODO: IMPL ENCHANTS SYSTEM <!> <!> enchantment(Enchantment.VANISHING_CURSE, 1)
                    } else {
                        name(buildComponent {
                            text("Change Type") {
                                it.color(Tailwind.AMBER_400)
                                it.decorate(TextDecoration.BOLD)
                            }
                        })
                    }
                }

                override fun onClick(player: FrameworkPlayer, type: ClickType) {
                    player.openMenu(MaterialCallbackMenu {
                        reference.type = it
                        player.openMenu(this@EditItemStackMenu)
                    })
                }
            },

            1 to object : AbstractInputButton() {
                override fun builder(player: FrameworkPlayer): AnvilGUI.Builder = AnvilGUI.Builder()
                    .plugin(Bukkit.getPluginManager().getPlugin("Framework"))
                    .text("Enter the new item name")
                    .onClick { _, event ->
                        reference = buildItem(PaperFrameworkItemStack(reference)) {
                            name(MiniMessage.miniMessage().deserialize(event.text))
                        }.item as ItemStack

                        player.openMenu(this@EditItemStackMenu)
                        return@onClick listOf(AnvilGUI.ResponseAction.close())
                    }

                override fun getMaterial(player: FrameworkPlayer) = XMaterial.OAK_SIGN.toMinecraftKey()

                override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit = {
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