package net.revive.framework.disguise

import net.revive.framework.PaperFrameworkPlugin
import net.revive.framework.disguise.events.DisguiseEvent
import net.revive.framework.disguise.events.PreDisguiseEvent
import net.revive.framework.disguise.events.UnDisguiseEvent
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.nms.disguise.DisguiseInfo
import net.revive.framework.nms.disguise.INMSDisguiseHandler
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

class FrameworkDisguiseHandler : IDisguiseHandler {

    @Inject
    lateinit var nmsDisguiseHandler: INMSDisguiseHandler

    private val originalGameProfiles = mutableMapOf<UUID, Any>()
    private val uuidToDisguiseInfo = mutableMapOf<UUID, DisguiseInfo>()

    override fun isDisguised(player: Player): Boolean {
        return uuidToDisguiseInfo[player.uniqueId] != null
    }

    override fun disguise(player: Player, disguiseInfo: DisguiseInfo, callback: () -> Unit) {
        if (PreDisguiseEvent(player, disguiseInfo).callEvent()) return

        originalGameProfiles[player.uniqueId] = nmsDisguiseHandler.getGameProfile(player)
        uuidToDisguiseInfo[player.uniqueId] = disguiseInfo

        nmsDisguiseHandler.handleDisguise(player, disguiseInfo)
        DisguiseEvent(player, disguiseInfo).callEvent()
        callback()

        player.setMetadata("disguised", FixedMetadataValue(PaperFrameworkPlugin.instance, true))
    }

    override fun unDisguise(player: Player, disconnecting: Boolean, callback: () -> Unit) {
        if (uuidToDisguiseInfo[player.uniqueId] == null) return

        nmsDisguiseHandler.handleUnDisguiseInternal(player, originalGameProfiles[player.uniqueId]!!, disconnecting)
        UnDisguiseEvent(player).callEvent()

        originalGameProfiles.remove(player.uniqueId)
        uuidToDisguiseInfo.remove(player.uniqueId)

        player.removeMetadata("disguised", PaperFrameworkPlugin.instance)

        if (!disconnecting) {
            nmsDisguiseHandler.reloadPlayerInternals(player)
        }
    }
}