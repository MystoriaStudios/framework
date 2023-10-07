package net.revive.framework.nms.disguise

interface INMSDisguiseHandler {
    fun getGameProfile(player: Any): Any
    fun handleDisguise(player: Any, disguiseInfo: DisguiseInfo)
    fun reloadPlayerInternals(player: Any)
    fun handleUnDisguiseInternal(player: Any, originalGameProfile: Any, disconnecting: Boolean)
}