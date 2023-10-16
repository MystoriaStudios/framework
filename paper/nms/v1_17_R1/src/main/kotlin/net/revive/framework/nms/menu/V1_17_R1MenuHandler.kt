package net.revive.framework.nms.menu

import com.google.common.base.Preconditions
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftContainer
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView

@NMSHandler(NMSVersion.V1_17_R1)
object V1_17_R1MenuHandler : INMSMenuHandler {

    override fun openCustomInventory(p: Any, inventory: Any, size: Int) {
        val player = (p as CraftPlayer).handle
        inventory as Inventory
        Preconditions.checkArgument(true, "Unknown windowType")
        var container: AbstractContainerMenu? = CraftContainer(inventory, player, player.nextContainerCounter())

        container = CraftEventFactory.callInventoryOpenEvent(player, container)
        if (container == null) return

        val `adventure$title` = container.bukkitView.title()

        if (!player.isImmobile()) player.connection.send(
            ClientboundOpenScreenPacket(
                container.containerId,
                getWindowType(size),
                PaperAdventure.asVanilla(`adventure$title`)
            )
        ) // Paper

        player.containerMenu = container
        player.initMenu(container)
    }

    override fun isSameInventory(inventory: Any, openInventory: Any, title: Any): Boolean {
        inventory as Inventory
        openInventory as InventoryView
        title as Component
        return openInventory.topInventory.size == inventory.size && openInventory.title() == title

    }

    private fun getWindowType(size: Int): MenuType<ChestMenu> {
        return when (size / 9) {
            1 -> MenuType.GENERIC_9x1;
            2 -> MenuType.GENERIC_9x2;
            3 -> MenuType.GENERIC_9x3;
            4 -> MenuType.GENERIC_9x4;
            5 -> MenuType.GENERIC_9x5;
            6 -> MenuType.GENERIC_9x6;
            else -> {
                throw UnsupportedOperationException("A window type was requested but no match was found, size: $size")
            }
        }
    }
}