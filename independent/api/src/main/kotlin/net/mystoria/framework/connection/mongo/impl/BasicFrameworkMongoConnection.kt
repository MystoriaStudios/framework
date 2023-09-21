package net.mystoria.framework.connection.mongo.impl

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection

class BasicFrameworkMongoConnection(
    private val details: Details
) : AbstractFrameworkMongoConnection() {
    data class Details(
        val uri: String = "mongodb://admin:a=@NbvLLa9?!D2tVL#nwt-eEe5CWB\$ky+C&3YxwWxNN@100.67.254.17:27017/admin",
        val database: String = "framework"
    ) {
        constructor(
            hostname: String,
            port: Int
        ) : this("mongodb://$hostname:$port/admin")

        constructor(
            hostname: String,
            port: Int,
            username: String,
            password: String
        ) : this("mongodb://$username:$password@$hostname:$port/admin")
    }

    override fun getAppliedResource() : MongoDatabase
    {
        return try {
            getConnection().getDatabase(details.database)
        } catch (ignored: Exception) {
            setConnection(createNewConnection())

            getConnection().getDatabase(details.database)
        }
    }

    override fun getConnection() = try { handle } catch (e: Exception) { createNewConnection() }
    override fun createNewConnection() = MongoClient(MongoClientURI(details.uri))
}