package net.revive.framework.grpc.health

import io.grpc.ManagedChannel
import net.revive.framework.Framework
import net.revive.framework.IFrameworkPlatform
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.protocol.Heartbeat
import net.revive.framework.protocol.HeartbeatServiceGrpc
import net.revive.framework.protocol.PodState
import net.revive.framework.server.IMinecraftPlatform

object PodHeartbeatThread : Thread("Framework-Pod Health Reporter") {

    @Inject
    lateinit var minecraftPlatform: IMinecraftPlatform

    @Inject
    lateinit var platform: IFrameworkPlatform

    @Inject
    lateinit var channel: ManagedChannel

    private val heartbeatService: HeartbeatServiceGrpc.HeartbeatServiceStub by lazy {
        HeartbeatServiceGrpc.newStub(channel)
    }

    @Configure
    fun configure() {
        sendHeartbeat(
            generateHeartbeat(
                PodState.BOOTING
            )
        )
    }

    @Close
    fun close() {
        sendHeartbeat(
            generateHeartbeat(
                PodState.OFFLINE
            )
        )
    }

    override fun run() {
        while (true) {
            sleep(2000L) // reports every 2 seconds to the parent node.
            sendHeartbeat(
                generateHeartbeat()
            )
        }
    }

    fun sendHeartbeat(heartbeat: Heartbeat) {
        Framework.use {
            it.log("Framework Heart Beater", "Sending heart beat!!")
        }

        heartbeatService.beat(heartbeat, null)
    }

    fun generateHeartbeat(state: PodState = PodState.ONLINE): Heartbeat {
        return Heartbeat
            .newBuilder()
            .setPod(platform.id)
            .setState(state)
            .setTps(0.00)
            .setMspt(0.00)
            .setCpuUsage(5.00)
            .setPlayersConnected(minecraftPlatform.getPlayerCount())
            .build()
    }
}