package net.revive.framework.metric.data

data class ServerMetricData(
    val time: Long,
    val onlinePlayers: Int,
    val tickData: TickMetricData,
    val worlds: List<WorldMetricData>
)