package net.mystoria.framework.menu.paged

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.mystoria.framework.menu.button.IButton
import net.mystoria.framework.utils.ItemStackBuilder
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
                    if (mod > 0) {
                        Component.text(
                            "Next Page",
                            NamedTextColor.GREEN
                        )
                    } else {
                        Component.text(
                            "Previous Page",
                            NamedTextColor.RED
                        )
                    }.append(
                        Component.text(
                            " (${menu.page + mod}/${menu.getPages(player)})",
                            NamedTextColor.GRAY
                        )
                    )
                }
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