package net.revive.framework

import net.revive.framework.instance.Instance

interface IFrameworkPlatform {

    val id: String
    val groups: MutableList<String>

    fun updateInstance(instance: Instance)
    fun hasGroup(group: String) = groups.contains(group)
}