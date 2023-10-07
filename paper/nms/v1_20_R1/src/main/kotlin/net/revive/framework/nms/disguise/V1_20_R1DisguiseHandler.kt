package net.revive.framework.nms.disguise

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameType
import net.minecraft.world.level.biome.BiomeManager
import net.revive.framework.nms.NMSVersion
import net.revive.framework.nms.annotation.NMSHandler
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import java.util.*

@NMSHandler(NMSVersion.V1_20_R1)
object V1_20_R1DisguiseHandler : INMSDisguiseHandler {
    override fun getGameProfile(player: Any): Any {
        return (player as CraftPlayer).handle.gameProfile
    }

    override fun handleDisguise(player: Any, disguiseInfo: DisguiseInfo) {
        player as CraftPlayer

        val newInfo = GameProfile(
            player.uniqueId,
            disguiseInfo.username
        ).apply {
            this.properties.removeAll("textures")
            this.properties.put(
                "textures",
                Property("textures", disguiseInfo.skinInfo, disguiseInfo.skinSignature)
            )
        }

        player.handle.gameProfile = newInfo

        reloadPlayerInternals(player)
    }

    override fun reloadPlayerInternals(player: Any) {
        player as CraftPlayer
        val previousLocation = player.location.clone()

        player.handle.connection.send(
            ClientboundPlayerInfoRemovePacket(
                mutableListOf(player.uniqueId)
            )
        )

        player.handle.connection.send(
            ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(
                    ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER
                ),
                ClientboundPlayerInfoUpdatePacket.Entry(
                    player.uniqueId,
                    player.profile,
                    true,
                    player.ping,
                    player.handle.gameMode.gameModeForPlayer,
                    player.handle.listName,
                    null
                )
            )
        )

        val worldServer = player.world as CraftWorld
        val level = MinecraftServer.getServer().getLevel(worldServer.handle.dimension()) ?: throw UnsupportedOperationException("WTF")

        player.handle.connection.send(
            ClientboundRespawnPacket(
                worldServer.handle.dimensionTypeId(),
                worldServer.handle.dimension(),
                BiomeManager.obfuscateSeed(worldServer.seed),
                player.handle.gameMode.gameModeForPlayer,
                player.handle.gameMode.previousGameModeForPlayer,
                level.isDebug,
                level.isFlat,
                ClientboundRespawnPacket.KEEP_ALL_DATA,
                player.handle.lastDeathLocation,
                player.handle.portalCooldown
            )
        )

        player.teleport(previousLocation)

        MinecraftServer
            .getServer()
            .playerList
            .players
            .apply {
                remove(player.handle)
                add(player.handle)
            }

    }

    // You need to get the gameprofile and save it before disguising a player TODO: make a method for it

    override fun handleUnDisguiseInternal(player: Any, originalGameProfile: Any, disconnecting: Boolean)
    {
        player as CraftPlayer
        player.handle.gameProfile = originalGameProfile as GameProfile

        if (disconnecting) {
            reloadPlayerInternals(player)
        }
    }
}