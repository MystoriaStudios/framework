package net.mystoria.framework

interface IFrameworkPlatform {

    val id: String
    val groups: MutableList<String>

    fun hasGroup(group: String) = groups.contains(group)
}