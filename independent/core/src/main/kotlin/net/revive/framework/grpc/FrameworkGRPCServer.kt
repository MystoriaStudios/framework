package net.revive.framework.grpc

import io.grpc.BindableService
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp
import net.revive.framework.flavor.service.Configure
import net.revive.framework.grpc.annotation.GRPCService
import net.revive.framework.utils.objectInstance

object FrameworkGRPCServer {

    private lateinit var server: Server

    @Configure
    fun configure()
    {
        server = ServerBuilder
            .forPort(FrameworkApp.settingsConfig.gRPCPort)
            .apply {
                Framework.use {
                    it.flavor.reflections
                        .getTypesAnnotatedWith<GRPCService>()
                        .mapNotNull { obj ->
                            obj.objectInstance()
                        }
                        .filterIsInstance(BindableService::class.java)
                        .forEach { service ->
                            this.addService(service)
                        }
                }
            }
            .build()
    }
}