package net.revive.framework.connection.mongo.impl

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import java.net.URLEncoder

class BasicFrameworkMongoConnection(
    private val details: Details
) : AbstractFrameworkMongoConnection() {
    data class Details(
        val uri: String = "mongodb://root:${
            URLEncoder.encode(
                System.getProperty("MONGODB_PASSWORD"),
                "UTF-8"
            )
        }@localhost:27017/admin",
        val database: String = "randomcraft"
    ) {
        constructor(
            hostname: String,
            port: Int
        ) : this("mongodb://$hostname:$port/admin")

        constructor(
            hostname: String,
            port: Int,
            username: String?,
            password: String?,
            database: String = "randomcraft",
            authDatabase: String = "admin",
            srv: Boolean = hostname.endsWith("mongodb.net")
        ) : this(
            "mongodb${
                if (srv) "+srv" else ""
            }://${
                if (username != null) "$username:" else ""
            }${
                if (password != null) "$password@" else ""
            }$hostname${if (!srv) ":$port" else ""}/$authDatabase", database
        )
    }

    override fun getAppliedResource(): MongoDatabase {
        return try {
            getConnection().getDatabase(details.database)
        } catch (ignoRED: Exception) {
            setConnection(createNewConnection())

            getConnection().getDatabase(details.database)
        }
    }

    override fun getConnection() = try {
        handle
    } catch (e: Exception) {
        createNewConnection()
    }

    override fun createNewConnection() = MongoClient(MongoClientURI(details.uri))
}