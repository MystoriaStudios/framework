package net.revive.framework.cache.impl.distribution

import net.revive.framework.cache.IUUIDCacheTranslator
import net.revive.framework.cache.UUIDCacheHelper
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

object DistributedRedisUUIDCache : IUUIDCacheTranslator {

    const val UUID_CACHE_KEY_UUID = "Framework:UUID"
    const val UUID_CACHE_KEY_USERNAME = "Framework:Username"

    private val redisConnection by lazy {
        net.revive.framework.Framework.useWithReturn {
            it.constructNewRedisConnection()
        }
    }

    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(username: String, uuid: UUID) {
        redisConnection.useResource {
            val async = this.async()
            async.hset(UUID_CACHE_KEY_UUID, uuid.toString(), username)
            async.hset(UUID_CACHE_KEY_USERNAME, username.lowercase(), uuid.toString())

            // TODO: Expire this on the independent server
        }
    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {
        redisConnection.useResource {
            val async = this.async()
            async.hset(UUID_CACHE_KEY_UUID, response.id.toString(), response.name)
            async.hset(UUID_CACHE_KEY_USERNAME, response.name.lowercase(), response.id.toString())

            // TODO: Expire this on the independent server
        }
    }

    override fun uniqueId(username: String): UUID? {
        return redisConnection.useResourceWithReturn {
            this.sync().hget(UUID_CACHE_KEY_USERNAME, username.lowercase()).let {
                UUID.fromString(it)
            }
        }
    }

    override fun username(uuid: UUID): String? {
        return redisConnection.useResourceWithReturn {
            this.sync().hget(UUID_CACHE_KEY_UUID, uuid.toString())
        }
    }
}