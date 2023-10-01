package net.mystoria.framework.nms.entity

interface INMSHologramProtocol {
    fun sendSpawnPacket(player: Any, id: Int, dataWatcher: Any, location: Any)
}