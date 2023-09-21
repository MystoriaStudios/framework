package net.mystoria.framework.module

import net.mystoria.framework.Framework

abstract class FrameworkModule(
    val details: ModuleDetails
) {

    inner class ModuleDetails(
        val name: String,
        val version: String,
        val mainClass: String,
    )

    fun load() {
        Framework.use {
            it.log(details.name, "Loading module with version ${details.version}")
        }
    }

    fun enable() {

    }

    fun disable() {

    }
}