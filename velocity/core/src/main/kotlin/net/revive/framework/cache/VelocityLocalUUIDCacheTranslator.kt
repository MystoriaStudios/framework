package net.revive.framework.cache

import com.velocitypowered.api.proxy.ProxyServer
import net.revive.framework.cache.impl.ILocalUUIDCacheTranslator
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.jvm.optionals.getOrNull

class VelocityLocalUUIDCacheTranslator(val server: ProxyServer) : ILocalUUIDCacheTranslator() {


    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(username: String, uuid: UUID) {

    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {

    }

    override fun uniqueId(username: String): UUID? {
        return server.getPlayer(username).getOrNull()?.uniqueId
    }

    override fun username(uuid: UUID): String? {
        return server.getPlayer(uuid).getOrNull()?.username
    }
}