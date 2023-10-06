package net.revive.framework.metric.data

import me.lucko.spark.api.statistic.StatisticWindow

data class TickMetricData(
    val ticksPerSecondFive: Double,
    val ticksPerSecondTen: Double,
    val ticksPerSecondMinute: Double,
    val milliSPerTickTen: Double
)