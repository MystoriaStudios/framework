package net.mystoria.framework.menu.paged

import com.cryptomorin.xseries.XMaterial
import net.mystoria.framework.menu.button.IButton
import net.mystoria.framework.utils.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class PageButton(private val mod: Int, private val menu: AbstractPagedMenu) : IButton {

    private fun hasNext(player: Player): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getMaterial(player: Player) = XMaterial.ARROW
    override fun getButtonItem(player: Player): ItemStack {
        return ItemBuilder {
            type(getMaterial(player).parseMaterial()!!)
            name(
                if (!hasNext(player)) {
                    " "
                } else {
                    (if (mod > 0) ChatColor.GREEN.toString() + (if (menu.verticalView) "Scroll down" else "Next page") else ChatColor.RED.toString() + (if (menu.verticalView) "Scroll up" else "Previous page")) + ChatColor.GRAY + " (${menu.page + mod}/${menu.getPages(player)})"
                }
            )
        }
    }

    override fun onClick(player: Player, type: ClickType) {
        when {
            hasNext(player) -> {
                menu.modPage(player, mod)
                //playNeutral(player)
            }
            else -> {

            }//playFail(player)
        }
    }
}