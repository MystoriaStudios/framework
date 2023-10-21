package net.revive.framework.menu.impl

import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.component.impl.CategoryComponent
import net.revive.framework.component.impl.ControlComponent
import net.revive.framework.constants.Tailwind
import net.revive.framework.menu.IMenu
import net.revive.framework.menu.button.IButton
import net.revive.framework.menu.button.impl.AbstractInputButton
import net.revive.framework.menu.openMenu
import net.revive.framework.menu.paged.AbstractPagedMenu
import net.revive.framework.metadata.IMetaDataHolder
import net.revive.framework.storage.storable.IStorable
import net.revive.framework.utils.ItemStackBuilder
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.toFramework
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class EditMetaDataMenu<T : IMetaDataHolder>(val holder: T, val save: (T) -> Unit) : AbstractPagedMenu() {

    override val metaData: IMenu.MetaData = IMenu.MetaData()
    override val buttonStartOffset = 9
    override val maxItemsPerPage = 21

    override fun getButtons(player: Player) = mutableMapOf<Int, IButton>().apply {
        this.putAll(super.getButtons(player))
        this[4] = object : AbstractInputButton() {
            override fun getMaterial(player: Player) = XMaterial.YELLOW_CANDLE

            override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                name(buildComponent {
                    text("Create new MetaData") {
                        it.color(Tailwind.AMBER_400)
                        it.decorate(TextDecoration.BOLD)
                    }
                })
                lore(
                    CategoryComponent("MetaData Control").build(),
                    Component.empty(),
                    buildComponent("Create a new key for metadata", Tailwind.GRAY_200),
                    buildComponent("attached to the holder.", Tailwind.GRAY_200),
                    Component.empty(),
                    ControlComponent(ClickType.LEFT.toFramework(), "create a new entry").build()
                )
            }

            override fun builder(player: Player) = AnvilGUI.Builder()
                .plugin(Bukkit.getPluginManager().getPlugin("Framework"))
                .text("Enter a key.")
                .onClick { _, event ->
                    if (holder has event.text) return@onClick listOf(
                        AnvilGUI.ResponseAction.replaceInputText("Key already exists.")
                    )

                    holder + (event.text to "")
                    save.invoke(holder)
                    player.openMenu(this@EditMetaDataMenu)
                    return@onClick listOf(AnvilGUI.ResponseAction.close())
                }
        }
    }

    /**
     * Retrieves a map of button positions to button identifiers for all pages of the menu.
     *
     * @param player The player for whom the buttons are generated.
     * @return A map of button positions (slot numbers) to button identifiers (IButton) for all pages.
     */
    override fun getAllPagesButtons(player: Player) = mutableMapOf<Int, IButton>().apply {
        holder.metaData.forEach {
            this[this.size] = object : AbstractInputButton() {
                override fun getMaterial(player: Player) = XMaterial.WRITABLE_BOOK
                override fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit = {
                    name(buildComponent {
                        text(it.key) {
                            it.color(Tailwind.AMBER_400)
                            it.decorate(TextDecoration.BOLD)
                        }
                    })

                    lore(
                        CategoryComponent("MetaData").build(),
                        Component.empty(),
                        buildComponent {
                            text(" • ", Tailwind.GRAY_700)
                            text("Key: ", Tailwind.AMBER_400)
                            text(it.key, Tailwind.TEAL_700)
                        },
                        buildComponent {
                            text(" • ", Tailwind.GRAY_700)
                            text("Value: ", Tailwind.AMBER_400)
                            text(it.value, Tailwind.TEAL_700)
                        },
                        Component.empty(),
                        ControlComponent(ClickType.LEFT.toFramework(), "edit the value").build(),
                        ControlComponent(ClickType.RIGHT.toFramework(), "delete the key").build()
                    )
                }

                override fun builder(player: Player) = AnvilGUI.Builder()
                    .plugin(Bukkit.getPluginManager().getPlugin("Framework"))
                    .text(it.value)
                    .onClick { _, event ->
                        holder + (it.key to event.text)
                        save.invoke(holder)

                        player.openMenu(this@EditMetaDataMenu)
                        return@onClick listOf(AnvilGUI.ResponseAction.close())
                    }

                override fun onClick(player: Player, type: ClickType) {
                    when (type) {
                        ClickType.LEFT -> super.onClick(player, type)
                        ClickType.RIGHT -> {
                            holder delete it.key
                            save.invoke(holder)
                        }

                        else -> return
                    }
                }
            }
        }
    }

    override fun getTitle(player: Player) = buildComponent(
        "Editing metadata ${
            if (holder is IStorable) {
                "for ${holder.identifier}"
            } else ""
        }", Tailwind.GRAY_700
    )
}