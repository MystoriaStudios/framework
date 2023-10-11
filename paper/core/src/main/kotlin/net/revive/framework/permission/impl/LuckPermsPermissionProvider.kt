package net.revive.framework.permission.impl

import co.aikar.commands.ConditionFailedException
import net.luckperms.api.LuckPerms
import net.revive.framework.permission.IPermissionProvider
import net.revive.framework.permission.PaperPermissionProvider
import java.util.*

class LuckPermsPermissionProvider(
    val luckPerms: LuckPerms
) : IPermissionProvider {

    override fun getPermissions(uuid: UUID): List<String> {
        return PaperPermissionProvider.getPermissions(uuid)
    }

    override fun hasPermission(uuid: UUID, permission: String): Boolean {
        return PaperPermissionProvider.hasPermission(uuid, permission)
    }

    override fun evaluate(uuid: UUID, test: String) {
        val rankId = if (!test.startsWith("rank.")) {
            PaperPermissionProvider.evaluate(uuid, test)
            return
        } else test.substring(4)

        if (!hasPermission(uuid, "group.${rankId}")) {
            val group = luckPerms.groupManager.getGroup(rankId) ?: run {
                PaperPermissionProvider.evaluate(uuid, test)
                null
            } ?: return
            throw ConditionFailedException("You require the ${group.displayName}§c group to do this.")
        }
    }
}