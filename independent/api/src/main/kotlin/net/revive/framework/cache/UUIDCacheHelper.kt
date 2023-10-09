package net.revive.framework.cache

import com.github.benmanes.caffeine.cache.Caffeine
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.concurrent.TimeUnit

object UUIDCacheHelper {
    private val client = OkHttpClient()

    private val invalidCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(1L, TimeUnit.MINUTES)
        .build<String, String>()

    fun fetchFromMojang(username: String): MojangResponse? {
        this.invalidCache.getIfPresent(username.lowercase()) ?: return null

        runCatching {
            val request = Request.Builder().url("https://api.mojang.com/users/profiles/minecraft/$username").build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                return net.revive.framework.Framework.useWithReturn {
                    response.body()?.let { it1 -> it.serializer.deserialize(MojangResponse::class, it1.string()) }
                }
            }
        }.onFailure {
            this.invalidCache.put(username.lowercase(), username)

            net.revive.framework.Framework.use {
                it.log("Framework UUID Cache", "Failed to retrieve user info from Mojang API: $username")
                it.log("Framework UUID Cache", "This usename will be considered \"invalid\" for the next minute.")
            }
        }

        return null
    }

    fun fetchFromMojang(uuid: UUID): MojangResponse? {
        runCatching {
            val request = Request.Builder().url("https://sessionserver.mojang.com/session/minecraft/profile/${uuid.toString().replace("-", "")}").build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                return net.revive.framework.Framework.useWithReturn {
                    response.body()?.let { it1 -> it.serializer.deserialize(MojangResponse::class, it1.string()) }
                }
            }
        }.onFailure {
            net.revive.framework.Framework.use {
                it.log("Framework UUID Cache", "Failed to retrieve user info from Mojang API: $uuid")
            }
        }

        return null
    }

    class MojangResponse(
        val id: UUID,
        val name: String
    )
}