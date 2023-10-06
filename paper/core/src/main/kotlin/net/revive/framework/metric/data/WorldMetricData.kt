package net.revive.framework.metric.data

import java.util.UUID

data class WorldMetricData(
    val name: String,
    val uid: UUID,
    val playerCount: Int,
    val entityCount: Int,
    val chunkCount: Int
)