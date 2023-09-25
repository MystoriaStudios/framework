package net.mystoria.framework.permission

import co.aikar.commands.ConditionFailedException
import java.util.*
import kotlin.jvm.Throws

interface IPermissionProvider<T> {

    fun hasPermission(uuid: UUID, permission: String): Boolean
    fun getPermissions(uuid: UUID): List<String>

    @Throws(ConditionFailedException::class)
    fun evaluate(uuid: UUID, test: String)

    @Throws(ConditionFailedException::class)
    fun evaluate(uuid: UUID, test: T)
}