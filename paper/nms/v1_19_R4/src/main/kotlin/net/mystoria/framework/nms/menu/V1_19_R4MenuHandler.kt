package net.mystoria.framework.nms.menu

import com.google.common.base.Preconditions
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R3.event.CraftEventFactory
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftContainer
import org.bukkit.inventory.Inventory


class V1_19_R4MenuHandler : INMSMenuHandler {

    override fun openCustomInventory(p: Any, inventory: Any, size: Int) {
        val player = (p as CraftPlayer).handle
        inventory as Inventory
        Preconditions.checkArgument(true, "Unknown windowType")
        var container: AbstractContainerMenu? = CraftContainer(inventory, player, player.nextContainerCounter())

        container = CraftEventFactory.callInventoryOpenEvent(player, container)
        if (container == null) return

        //String title = container.getBukkitView().getTitle(); // Paper - comment

        //String title = container.getBukkitView().getTitle(); // Paper - comment
        val `adventure$title` = container.bukkitView.title() // Paper

        //player.connection.send(new ClientboundOpenScreenPacket(container.containerId, windowType, CraftChatMessage.fromString(title)[0])); // Paper - comment

        //player.connection.send(new ClientboundOpenScreenPacket(container.containerId, windowType, CraftChatMessage.fromString(title)[0])); // Paper - comment
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