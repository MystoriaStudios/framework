package net.mystoria.framework.permission

import co.aikar.commands.ConditionFailedException
import org.bukkit.Bukkit
import java.util.*

object PaperPermissionProvider : IPermissionProvider {

    override fun hasPermission(uuid: UUID, permission: String) = Bukkit.getPlayer(uuid)?.hasPermission(permission) ?: false

    override fun getPermissions(uuid: UUID) = Bukkit.getPlayer(uuid)?.effectivePermissions?.map {
        it.permission
    } ?: emptyList()

    override fun evaluate(uuid: UUID, test: String) {
        val player = Bukkit.getPlayer(uuid) ?: return
        if (!player.hasPermission(test)) throw ConditionFailedException("You are not authorized to do this.")
    }
}