package net.revive.framework.grpc.health

import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import net.revive.framework.Framework
import net.revive.framework.IFrameworkPlatform
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.protocol.*
import net.revive.framework.server.IMinecraftPlatform

object ContainerHeartbeatThread : Thread("Framework-Container Health Reporter") {

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
                ContainerState.STARTING
            )
        )
    }

    @Close
    fun close() {
        sendHeartbeat(
            generateHeartbeat(
                ContainerState.STOPPING
            )
        )
    }

    override fun run() {
        while (true) {
            sleep(3000L) // reports every 2 seconds to the parent node.
            sendHeartbeat(
                generateHeartbeat()
            )
        }
    }

    fun sendHeartbeat(heartbeat: Heartbeat) {
        Framework.use {
            it.log("Framework Heart Beater", "Sending heart beat!!")
        }

        heartbeatService.beat(heartbeat, EMPTY_RESPONSE_OBSERVER)
    }

    fun generateHeartbeat(state: ContainerState = ContainerState.RUNNING): Heartbeat {
        return Heartbeat
            .newBuilder()
            .setContainer(platform.id)
            .setState(state)
            .setTps(0.00)
            .setMspt(0.00)
            .setCpuUsage(5.00)
            .setPlayersConnected(minecraftPlatform.getPlayerCount())
            .setTimestamp(System.currentTimeMillis())
            .build()
    }

    val EMPTY_RESPONSE_OBSERVER = object : StreamObserver<Empty> {
        override fun onNext(value: Empty?) {

        }

        override fun onError(t: Throwable?) {
            t?.printStackTrace()
        }

        override fun onCompleted() {
            Framework.use {
                it.log("Framework Heart Beater", "Successfully sent heartbeat and received success!")
            }
        }

    }
}