package net.revive.framework.grpc.heartbeat

import net.revive.framework.Framework
import net.revive.framework.grpc.annotation.GRPCService
import net.revive.framework.heartbeat.HeartbeatService
import net.revive.framework.node.WrappedPodHeartbeat
import net.revive.framework.protocol.Empty
import net.revive.framework.protocol.Heartbeat
import net.revive.framework.protocol.HeartbeatServiceGrpcKt

@GRPCService
object PodHeartbeatService : HeartbeatServiceGrpcKt.HeartbeatServiceCoroutineImplBase() {

    override suspend fun beat(request: Heartbeat): Empty {
        Framework.use {
            it.log("Pod Heartbeat Service", "Received Heartbeat from pod: ${request.pod}, state: ${request.state}")
        }

        HeartbeatService.podBeats[request.pod] = WrappedPodHeartbeat(
            request.pod,
            WrappedPodHeartbeat.State.valueOf(request.state.name),
            request.tps,
            request.mspt,
            request.cpuUsage,
            request.playersConnected
        )

        return Empty.getDefaultInstance()
    }
}