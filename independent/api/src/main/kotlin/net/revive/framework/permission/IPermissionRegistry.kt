package net.revive.framework.permission

interface IPermissionRegistry {

    fun getRegisteredPermissions() : List<String>
}