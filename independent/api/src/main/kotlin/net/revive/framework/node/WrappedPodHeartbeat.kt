package net.revive.framework.node

data class WrappedPodHeartbeat(
    val pod: String,
    val state: State,
    val tps: Double,
    val mspt: Double,
    val cpuUsage: Double,
    val playersConnected: Int
) {
    enum class State {
        BOOTING,
        SETUP,
        ONLINE,
        OFFLINE
    }
}