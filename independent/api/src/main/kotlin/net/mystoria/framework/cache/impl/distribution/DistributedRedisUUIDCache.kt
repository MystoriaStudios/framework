package net.mystoria.framework.cache.impl.distribution

import net.mystoria.framework.Framework
import net.mystoria.framework.cache.IUUIDCacheTranslator
import net.mystoria.framework.cache.UUIDCacheHelper
import net.mystoria.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.mystoria.framework.flavor.annotation.Inject
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class DistributedRedisUUIDCache : IUUIDCacheTranslator
{

    @Inject
    lateinit var redisConnection: BasicFrameworkRedisConnection

    override val uniqueIdCache: MutableMap<UUID, String> = mutableMapOf()
    override val usernameCache: MutableMap<String, UUID> = mutableMapOf()

    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {
        Framework.use {

        }
    }

    override fun uniqueId(username: String): UUID? {
        TODO("Not yet implemented")
    }

    override fun username(uuid: UUID): String? {
        TODO("Not yet implemented")
    }
}