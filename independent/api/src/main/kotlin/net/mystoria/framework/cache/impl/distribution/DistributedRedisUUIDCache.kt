package net.mystoria.framework.cache.impl.distribution

import io.lettuce.core.ExpireArgs
import net.mystoria.framework.Framework
import net.mystoria.framework.cache.IUUIDCacheTranslator
import net.mystoria.framework.cache.UUIDCacheHelper
import net.mystoria.framework.connection.redis.impl.BasicFrameworkRedisConnection
import net.mystoria.framework.flavor.annotation.Inject
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.math.exp

class DistributedRedisUUIDCache : IUUIDCacheTranslator
{

    companion object {
        const val UUID_CACHE_KEY_UUID = "FRAMEWORK-UUID-CACHE-UUID"
        const val UUID_CACHE_KEY_USERNAME = "FRAMEWORK-UUID-CACHE-USERNAME"
    }

    private val redisConnection by lazy {
        Framework.useWithReturn {
            it.constructNewRedisConnection()
        }
    }

    override val uniqueIdCache: MutableMap<UUID, String> = mutableMapOf()
    override val usernameCache: MutableMap<String, UUID> = mutableMapOf()

    override fun configure(): CompletionStage<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun preLoadCache(async: Boolean) {

    }

    override fun update(response: UUIDCacheHelper.MojangResponse, commit: Boolean) {
        redisConnection.useResource {
            val async = this.async()
            async.hset(UUID_CACHE_KEY_UUID, response.id.toString(), response.name)
            async.hset(UUID_CACHE_KEY_USERNAME, response.name.lowercase(), response.id.toString())

            // TODO: Expire this
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