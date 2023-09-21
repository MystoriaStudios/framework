package net.mystoria.framework.permission

interface IPermissionRegistry {

    fun getRegisteredPermissions() : List<String>
}