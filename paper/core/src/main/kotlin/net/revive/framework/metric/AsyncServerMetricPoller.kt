package net.revive.framework.metric

import net.revive.framework.flavor.service.Configure
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object AsyncServerMetricPoller {

    @Configure
    fun configure()
    {
        thread(
            start = true,
            name = "server-metric-poller"
        ) {
            while (true) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(5))
                ServerMetricService.updateRedisData(
                    ServerMetricService.getUpdatedMetadata()
                )
            }
        }
    }
}