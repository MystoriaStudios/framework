package net.revive.framework.menu.paged

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.menu.button.IButton
import net.revive.framework.utils.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class PageButton(private val mod: Int, private val menu: AbstractPagedMenu) : IButton {

    private fun hasNext(player: Player): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getMaterial(player: Player) = XMaterial.ARROW
    override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit {
        return {
            name(
                if (!hasNext(player)) {
                    Component.empty()
                } else {
                    Component.text(
                        if (mod > 0) {
                            "Next Page"
                        } else {
                            "Previous Page"
                        },
                        TextColor.fromHexString("#ffae42")
                    ).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
                }
            )

            lore(
                Component.text("View the ${if (mod > 0) "next" else "previous"} menu page.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text(
                    "Click the view the ${if (mod > 0) "next" else "previous"} page!",
                    TextColor.fromHexString("#ffae42")
                ).decoration(TextDecoration.ITALIC, false)
            )
        }
    }

    override fun onClick(player: Player, type: ClickType) {
        when {
            hasNext(player) -> {
                menu.modPage(player, mod)

                XSound.BLOCK_AMETHYST_BLOCK_CHIME.play(player)
            }
            else -> {
                XSound.BLOCK_GRAVEL_BREAK.play(player)
            }
        }
    }
}