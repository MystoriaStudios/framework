package net.revive.framework.nms.entity

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player

@NMSHandler(NMSVersion.V1_20_R1)
object V1_20_R1EntityHandler : INMSEntityHandler {
    override fun sendDestroyPackets(player: Any, vararg id: Int) = send(
        player as Player,
        ClientboundRemoveEntitiesPacket(
            *id
        )
    )

    override fun sendEntityTeleport(player: Any, entityId: Int, location: Any, yaw: Byte, pitch: Byte) {
        player as Player
        location as Location
        send(
            player,
            ClientboundTeleportEntityPacket(
                FriendlyByteBuf(Unpooled.buffer()).apply {
                    this.writeVarInt(entityId)
                    this.writeDouble(location.x)
                    this.writeDouble(location.y)
                    this.writeDouble(location.z)
                    this.writeByte(yaw.toInt())
                    this.writeByte(pitch.toInt())
                    this.writeBoolean(true)
                    release()
                }
            )
        )
    }

    override fun sendStatusPacket(vararg player: Any, entityId: Int, status: Byte) {
        val packet = ClientboundEntityEventPacket(
            FriendlyByteBuf(Unpooled.buffer()).apply {
                this.writeInt(entityId)
                this.writeByte(status.toInt())
            }
        )

        player.forEach {
            it as Player
            send(it, packet)
        }
    }

    override fun sendStatusPacket(player: List<Any>, entityId: Int, status: Byte) {
        val packet = ClientboundEntityEventPacket(
            FriendlyByteBuf(Unpooled.buffer()).apply {
                this.writeInt(entityId)
                this.writeByte(status.toInt())
            }
        )

        player.forEach {
            it as Player
            send(it, packet)
        }
    }

    private fun send(player: Player, packet: Packet<*>) {
        (player as CraftPlayer).handle.connection.send(packet)
    }
}