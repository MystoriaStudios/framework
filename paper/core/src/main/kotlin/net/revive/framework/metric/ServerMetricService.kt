package net.revive.framework.metric

import me.lucko.spark.api.SparkProvider
import me.lucko.spark.api.statistic.StatisticWindow
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Service
import net.revive.framework.metric.data.ServerMetricData
import net.revive.framework.metric.data.TickMetricData
import net.revive.framework.metric.data.WorldMetricData
import org.bukkit.Bukkit

@Service
object ServerMetricService {

    @Inject
    lateinit var redisConnection: AbstractFrameworkRedisConnection

    val spark by lazy {
        return@lazy SparkProvider.get()
    }

    fun updateRedisData(metaData: ServerMetricData)
    {
        redisConnection.useResource {
            /* TODO: Figure out how to handle server ids
            this.async().hset(
                "servers",
                //"metadunknownata"
            )

             */
        }
    }

    fun getUpdatedMetadata(): ServerMetricData
    {
        val tps = spark.tps()!!
        val mspt = spark.mspt()!!

        return ServerMetricData(
            time = System.currentTimeMillis(),
            onlinePlayers = Bukkit.getOnlinePlayers().size,
            tickData = TickMetricData(
                tps.poll(StatisticWindow.TicksPerSecond.SECONDS_5),
                tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10),
                tps.poll(StatisticWindow.TicksPerSecond.MINUTES_1),
                mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10).max()
            ),
            worlds = Bukkit.getWorlds().map { world ->
                WorldMetricData(
                    world.name,
                    world.uid,
                    world.playerCount,
                    world.entityCount,
                    world.chunkCount
                )
            }
        )
    }
}