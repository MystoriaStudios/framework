package net.revive.framework.connection.redis.impl

import io.lettuce.core.RedisClient
import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection

class BasicFrameworkRedisConnection(
    private val details: Details
) : AbstractFrameworkRedisConnection() {
    data class Details(
        val uri: String
    ) {
        constructor(
            hostname: String = "127.0.0.1",
            port: Int = 6379,
        ) : this("redis://$hostname:$port")

        constructor(
            hostname: String = "127.0.0.1",
            port: Int = 6379,
            username: String? = null,
            password: String
        ) : this("redis://${username.let { "$it:" }}$password@$hostname:$port")
    }

    override fun createNewConnection() = RedisClient.create(details.uri)
}
