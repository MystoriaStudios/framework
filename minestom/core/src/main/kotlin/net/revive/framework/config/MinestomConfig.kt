package net.revive.framework.config

import com.google.gson.annotations.SerializedName

@JsonConfig("minestom.json")
class MinestomConfig(
    var port: Int = 25565,

    @SerializedName("mongo_details")
    var mongoDetails: Mongo = Mongo(),

    @SerializedName("redis_details")
    var redisDetails: Redis = Redis()
) {
    data class Mongo(
        var uri: String = "mongodb://localhost:27017",
        var database: String = "framework"
    )

    data class Redis(
        var host: String = "127.0.0.1",
        var port: Int = 6379
    )
}