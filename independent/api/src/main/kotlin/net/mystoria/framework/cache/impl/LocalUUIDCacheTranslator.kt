package net.mystoria.framework.cache.impl

import net.mystoria.framework.cache.IUUIDCacheTranslator
import net.mystoria.framework.cache.UUIDCache
import net.mystoria.framework.cache.UUIDCacheHelper
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

object LocalUUIDCacheTranslator : IUUIDCacheTranslator
{
    override val uniqueIdCache: MutableMap<UUID, String> = ConcurrentHashMap()
    override val usernameCache: MutableMap<String, UUID> = ConcurrentHashMap()

    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {
        usernameCache[response.name.lowercase()] = response.id
        uniqueIdCache[response.id] = response.name
    }

    override fun uniqueId(username: String): UUID? {
        return usernameCache[username.lowercase()]
    }

    override fun username(uuid: UUID): String? {
        return uniqueIdCache[uuid]
    }

}