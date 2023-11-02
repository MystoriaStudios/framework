package net.revive.framework.grpc.heartbeat

import net.revive.framework.Framework
import net.revive.framework.grpc.annotation.GRPCService
import net.revive.framework.protocol.Empty
import net.revive.framework.protocol.Heartbeat
import net.revive.framework.protocol.HeartbeatServiceGrpcKt

@GRPCService
object PodHeartbeatService : HeartbeatServiceGrpcKt.HeartbeatServiceCoroutineImplBase() {

    override suspend fun beat(request: Heartbeat): Empty {
        // handle any heart beat logic here...
        Framework.use {
            it.log("Pod Heartbeat Service", "Received Heartbeat from pod: ${request.pod}, state: ${request.state}")
        }

        return Empty.getDefaultInstance()
    }
}