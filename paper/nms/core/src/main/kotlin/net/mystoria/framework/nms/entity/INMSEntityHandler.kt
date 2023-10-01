package net.mystoria.framework.nms.entity

interface INMSEntityHandler {
    fun sendDestroyPackets(player: Any, vararg id: Int)
    fun sendEntityTeleport(player: Any, entityId: Int, location: Any, yaw: Byte, pitch: Byte)
    fun sendStatusPacket(vararg player: Any, entityId: Int, status: Byte)
    fun sendStatusPacket(player: List<Any>, entityId: Int, status: Byte)
}