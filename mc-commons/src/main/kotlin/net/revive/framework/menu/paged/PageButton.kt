package net.revive.framework.menu.paged

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.FrameworkPlayer

class PageButton(private val mod: Int, private val menu: AbstractPagedMenu) : IButton {

    private fun hasNext(player: FrameworkPlayer): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getMaterial(player: FrameworkPlayer) = if (hasNext(player)) XMaterial.HONEYCOMB else XMaterial.AIR
    override fun getButtonItem(player: FrameworkPlayer): ItemStackBuilder.() -> Unit {
        if (!hasNext(player)) return {}
        return {
            name(
                Component.text(
                    if (mod > 0) {
                        "Next Page"
                    } else {
                        "Previous Page"
                    },
                    TextColor.fromHexString("#ffae42")
                ).decorate(TextDecoration.BOLD)

            )
            flags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
        }
    }

    override fun onClick(player: Player, type: ClickType) {
        when {
            hasNext(player) -> {
                menu.modPage(player, mod)

                XSound.BLOCK_AMETHYST_BLOCK_CHIME.play(player, 4f, 1f)
            }

            else -> {
                XSound.BLOCK_GRAVEL_BREAK.play(player)
            }
        }
    }
}