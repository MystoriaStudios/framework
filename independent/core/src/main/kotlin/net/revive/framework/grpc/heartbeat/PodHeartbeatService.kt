package net.revive.framework.grpc.heartbeat

import net.revive.framework.protocol.Empty
import net.revive.framework.protocol.Heartbeat
import net.revive.framework.protocol.HeartbeatServiceGrpcKt


class PodHeartbeatService : HeartbeatServiceGrpcKt.HeartbeatServiceCoroutineImplBase() {

    override suspend fun beat(request: Heartbeat): Empty {
        // handle any heart beat logic here...
        Heartbeat
            .newBuilder()

        return Empty.getDefaultInstance()
    }
}