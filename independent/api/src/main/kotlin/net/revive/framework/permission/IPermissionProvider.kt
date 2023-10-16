package net.revive.framework.permission

import co.aikar.commands.ConditionFailedException
import java.util.*

interface IPermissionProvider {
    fun hasPermission(uuid: UUID, permission: String): Boolean
    fun getPermissions(uuid: UUID): List<String>

    @Throws(ConditionFailedException::class)
    fun evaluate(uuid: UUID, test: String)
}