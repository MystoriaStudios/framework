package net.revive.framework.grpc.heartbeat

import net.revive.framework.Framework
import net.revive.framework.grpc.annotation.GRPCService
import net.revive.framework.heartbeat.HeartbeatService
import net.revive.framework.node.WrappedContainerHeartbeat
import net.revive.framework.protocol.Empty
import net.revive.framework.protocol.Heartbeat
import net.revive.framework.protocol.HeartbeatServiceGrpcKt

@GRPCService
object ContainerHeartbeatService : HeartbeatServiceGrpcKt.HeartbeatServiceCoroutineImplBase() {

    override suspend fun beat(request: Heartbeat): Empty {
        Framework.use {
            it.log("Pod Heartbeat Service", "Received Heartbeat from container: ${request.container}, state: ${request.state}")
        }

        HeartbeatService.podBeats[request.container] = WrappedContainerHeartbeat(
            request.container,
            WrappedContainerHeartbeat.State.valueOf(request.state.name),
            request.tps,
            request.mspt,
            request.cpuUsage,
            request.playersConnected,
            request.timestamp
        )

        return Empty.getDefaultInstance()
    }
}