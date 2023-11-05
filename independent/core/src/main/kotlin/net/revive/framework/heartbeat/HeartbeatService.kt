package net.revive.framework.heartbeat

import net.revive.framework.Framework
import net.revive.framework.FrameworkApp
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.node.Node
import net.revive.framework.node.WrappedPodHeartbeat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
object HeartbeatService {

    val heartbeat: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    val podBeats: MutableMap<String, WrappedPodHeartbeat> = mutableMapOf()

    @Configure
    fun configure()
    {
        Framework.instance.log("Heartbeat", "Starting the node heartbeat.")

        heartbeat.scheduleAtFixedRate({
              beat()
        }, 0, 5, TimeUnit.SECONDS)
    }

    fun beat(state: Node.State? = null) {
        val iterator = podBeats.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val beat = entry.value

            if (System.currentTimeMillis() < beat.timestamp.plus(TimeUnit.MINUTES.toMillis(3))) {
                // Remove node
                iterator.remove() // Safe removal using the iterator
            }
        }

        Framework.use {
            val request = Request.Builder()
                .url("https://api.nopox.xyz/api/nodes/${FrameworkApp.settingsConfig.api_key}/add")
                .post(
                    it.serializer.serialize(
                        Node(
                            FrameworkApp.settingsConfig.id,
                            FrameworkApp.settingsConfig.publicAddress,
                            FrameworkApp.settingsConfig.api_key,
                            state ?: FrameworkApp.settingsConfig.runningState,
                            System.currentTimeMillis(),
                            FrameworkApp.settingsConfig.identifier
                        )
                    ).toRequestBody("text/json".toMediaType())
                )
                .build()

            it.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    it.log("Heartbeat Error", "${response.code} - ${response.message}\"")
                }
            }
        }
    }
}