package net.mystoria.framework.permission

import org.bukkit.Bukkit

object PaperPermissionRegistry : IPermissionRegistry {
    override fun getRegisteredPermissions(): List<String> {
        return Bukkit.getServer().pluginManager.permissions.map {
            it.name
        }
    }
}