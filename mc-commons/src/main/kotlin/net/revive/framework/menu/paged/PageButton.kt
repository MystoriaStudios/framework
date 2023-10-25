package net.revive.framework.menu.paged

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.component.ClickType
import net.revive.framework.item.FrameworkItemFlag
import net.revive.framework.item.ItemStackBuilder
import net.revive.framework.key.MinecraftKey
import net.revive.framework.menu.button.IButton
import net.revive.framework.sender.FrameworkPlayer

class PageButton(private val mod: Int, private val menu: AbstractPagedMenu) : IButton {

    private fun hasNext(player: FrameworkPlayer): Boolean {
        val pg = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getMaterial(player: FrameworkPlayer) =
        if (hasNext(player)) MinecraftKey("honeycomb") else MinecraftKey("air")

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
            flag(FrameworkItemFlag.HIDE_ITEM_SPECIFICS, FrameworkItemFlag.HIDE_ATTRIBUTES)
        }
    }

    override fun onClick(player: FrameworkPlayer, type: ClickType) {
        when {
            hasNext(player) -> {
                menu.modPage(player, mod)
                player.playSound(
                    Sound.sound(
                        MinecraftKey("block.large_amethyst_bud.place"),
                        Sound.Source.BLOCK,
                        4f,
                        1f
                    )
                )
                //XSound.BLOCK_AMETHYST_BLOCK_CHIME.play(player, 4f, 1f)
            }

            else -> {
                player.playSound(
                    Sound.sound(
                        MinecraftKey("block.suspicious_gravel.break"),
                        Sound.Source.BLOCK,
                        4f,
                        1f
                    )
                )

                //XSound.BLOCK_GRAVEL_BREAK.play(player)
            }
        }
    }
}