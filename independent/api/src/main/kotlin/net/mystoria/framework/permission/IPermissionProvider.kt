package net.mystoria.framework.permission

import java.util.UUID

interface IPermissionProvider {

    fun hasPermission(uuid: UUID) : Boolean
    fun getPermissions(uuid: UUID) : List<String>
}