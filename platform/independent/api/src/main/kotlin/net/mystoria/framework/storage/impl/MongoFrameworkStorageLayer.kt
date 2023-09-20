package net.mystoria.framework.storage.impl

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import net.mystoria.framework.Framework
import net.mystoria.framework.connection.mongo.AbstractFrameworkMongoConnection
import net.mystoria.framework.controller.FrameworkObjectController
import net.mystoria.framework.storage.FrameworkStorageLayer
import net.mystoria.framework.storage.storable.Storable
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

class MongoFrameworkStorageLayer<D : Storable>(
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
            Framework.use {
                entries[UUID.fromString(document.getString("_id"))!!] = it.serializer.deserialize(dataType, document.toJson())
            }
        }

        return entries
    }

    override fun loadWithFilterSync(filter: Bson): D?
    {
        val document = collection.find(filter)
            .first() ?: return null

        return Framework.useWithReturn {
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
                    Framework.useWithReturn {
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

        return Framework.useWithReturn {
            it.serializer.deserialize(dataType, document.toJson())
        }
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        for (document in collection.find())
        {
            entries[UUID.fromString(document.getString("_id"))!!] = Framework.useWithReturn {
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
