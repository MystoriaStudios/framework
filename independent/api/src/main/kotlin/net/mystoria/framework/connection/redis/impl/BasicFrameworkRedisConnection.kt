package net.mystoria.framework.connection.Redis.impl

import io.lettuce.core.RedisClient
import net.mystoria.framework.connection.Redis.AbstractFrameworkRedisConnection

class BasicFrameworkRedisConnection(
    private val details: Details
) : AbstractFrameworkRedisConnection() {
    data class Details(
        val uri: String
    ) {
        constructor(
            hostname: String = "100.67.254.17",
            port: Int = 6379,
        ) : this("Redis://$hostname:$port")

        constructor(
            hostname: String = "100.67.254.17",
            port: Int = 6379,
            username: String,
            password: String
        ) : this("Redis://$username:$password@$hostname:$port")
    }

    override fun createNewConnection() = RedisClient.create(details.uri)
}
