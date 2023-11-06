package net.revive.framework.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import net.revive.framework.Framework
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.grpc.health.ContainerHeartbeatThread
import net.revive.framework.server.IMinecraftPlatform

object FrameworkGRPCClient {

    @Inject
    lateinit var minecraftPlatform: IMinecraftPlatform

    fun configure()
    {
        val client = ManagedChannelBuilder
            .forAddress(
                minecraftPlatform.getNodeIP(),
                minecraftPlatform.getNodePort()
            )
            .usePlaintext()
            .build()

        Framework.use {
            it.flavor.bind<ManagedChannel>() to client
            it.flavor.inject(ContainerHeartbeatThread)
        }

        ContainerHeartbeatThread.apply {
            Framework.use {
                start()
            }
        }
    }
}