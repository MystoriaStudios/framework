package net.revive.framework.utils

import com.cryptomorin.xseries.XMaterial
import net.revive.framework.key.MinecraftKey
import org.bukkit.Material

fun Material.toMinecraftKey(): MinecraftKey {
    return MinecraftKey(key.key)
}

fun XMaterial.toMinecraftKey(): MinecraftKey {
    return this.parseMaterial()?.let {
        MinecraftKey(it.key.key)
    }!!
}