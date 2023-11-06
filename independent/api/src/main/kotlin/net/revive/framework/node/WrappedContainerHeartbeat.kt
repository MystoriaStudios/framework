package net.revive.framework.node

data class WrappedContainerHeartbeat(
    val pod: String,
    val state: State,
    val tps: Double,
    val mspt: Double,
    val cpuUsage: Double,
    val playersConnected: Int,
    val timestamp: Long
) {
    enum class State {
        BOOTING,
        SETUP,
        ONLINE,
        OFFLINE
    }
}