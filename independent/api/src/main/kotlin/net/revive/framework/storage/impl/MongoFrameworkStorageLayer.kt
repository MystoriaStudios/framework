package net.revive.framework.storage.impl

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import net.revive.framework.Framework
import net.revive.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.revive.framework.controller.FrameworkObjectController
import net.revive.framework.storage.FrameworkStorageLayer
import net.revive.framework.storage.storable.IStorable
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

class MongoFrameworkStorageLayer<D : IStorable>(
    connection: AbstractFrameworkMongoConnection,
    private val container: FrameworkObjectController<D>,
    private val dataType: KClass<D>
) : FrameworkStorageLayer<AbstractFrameworkMongoConnection, D, Bson>(connection)
{
    private var collection by Delegates.notNull<MongoCollection<Document>>()
    private val upsetOptions = UpdateOptions().upsert(true)

    init
    {
        collection = connection
            .useResourceWithReturn {
                this.getCollection(dataType.simpleName!!)
            }
    }

    fun withCustomCollection(
        collection: String
    )
    {
        this.collection = connection
            .useResourceWithReturn {
                this.getCollection(collection)
            }
    }

    override fun loadAllWithFilterSync(
        filter: Bson
    ): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        for (document in collection.find(filter))  {
            net.revive.framework.Framework.use {
                entries[UUID.fromString(document.getString("_id"))!!] = it.serializer.deserialize(dataType, document.toJson())
            }
        }

        return entries
    }

    override fun loadWithFilterSync(filter: Bson): D?
    {
        val document = collection.find(filter)
            .first() ?: return null

        return net.revive.framework.Framework.useWithReturn {
            it.serializer.deserialize(dataType, document.toJson())
        }
    }

    override fun saveSync(data: D)
    {
        collection.updateOne(
            Filters.eq(
                "_id", data.identifier.toString()
            ),
            Document(
                "\$set",
                Document.parse(
                    net.revive.framework.Framework.useWithReturn {
                        it.serializer.serialize(data)
                    }
                )
            ),
            upsetOptions
        )
    }

    override fun loadSync(identifier: UUID): D?
    {
        val document = collection.find(
            Filters.eq("_id", identifier.toString())
        ).first() ?: return null

        return net.revive.framework.Framework.useWithReturn {
            it.serializer.deserialize(dataType, document.toJson())
        }
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        for (document in collection.find())
        {
            entries[UUID.fromString(document.getString("_id"))!!] = net.revive.framework.Framework.useWithReturn {
                it.serializer.deserialize(dataType, document.toJson())
            }
        }

        return entries
    }

    override fun deleteSync(identifier: UUID)
    {
        collection.deleteOne(
            Filters.eq(
                "_id",
                identifier.toString()
            )
        )
    }
}
