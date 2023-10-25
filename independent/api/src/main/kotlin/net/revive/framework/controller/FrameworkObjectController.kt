package net.revive.framework.controller

import jdk.jfr.Timestamp
import net.revive.framework.debug
import net.revive.framework.storage.FrameworkStorageLayer
import net.revive.framework.storage.impl.CachedFrameworkStorageLayer
import net.revive.framework.storage.impl.MongoFrameworkStorageLayer
import net.revive.framework.storage.impl.RedisFrameworkStoreStorageLayer
import net.revive.framework.storage.storable.IStorable
import net.revive.framework.storage.type.FrameworkStorageType
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class FrameworkObjectController<D : IStorable>(
    private val dataType: KClass<D>
) {
    val localLayerCache = mutableMapOf<FrameworkStorageType, FrameworkStorageLayer<*, D, *>>()

    private var timestampField: Field? = null
    private var timestampThreshold = 2000L

    fun cache(): CachedFrameworkStorageLayer<D> =
        localLayerCache[FrameworkStorageType.CACHE] as CachedFrameworkStorageLayer<D>?
            ?: throw NullPointerException()

    fun redis(): RedisFrameworkStoreStorageLayer<D> =
        localLayerCache[FrameworkStorageType.REDIS] as RedisFrameworkStoreStorageLayer<D>?
            ?: throw NullPointerException()

    fun mongo(): MongoFrameworkStorageLayer<D> =
        localLayerCache[FrameworkStorageType.MONGO] as MongoFrameworkStorageLayer<D>?
            ?: throw NullPointerException()


    fun preLoadResources() {
        net.revive.framework.Framework.use {
            localLayerCache[FrameworkStorageType.MONGO] = MongoFrameworkStorageLayer(
                it.constructNewMongoConnection(), this, dataType
            )

            localLayerCache[FrameworkStorageType.REDIS] = RedisFrameworkStoreStorageLayer(
                it.constructNewRedisConnection(), this, dataType
            )
        }

        this.timestampField = dataType
            .java.fields.firstOrNull {
                it.isAnnotationPresent(Timestamp::class.java)
            }

        localLayerCache[FrameworkStorageType.CACHE] = CachedFrameworkStorageLayer()
    }

    fun setupCache(type: FrameworkStorageType) {
        localLayerCache[type]?.loadAll()?.thenAccept { it ->
            it.values.forEach(localLayerCache[FrameworkStorageType.CACHE]!!::save)
        }
    }

    fun localCache(): ConcurrentHashMap<UUID, D> {
        return useLayerWithReturn<CachedFrameworkStorageLayer<D>, ConcurrentHashMap<UUID, D>>(
            FrameworkStorageType.CACHE
        ) {
            this.connection.handle
        }
    }

    inline fun <reified T : FrameworkStorageLayer<*, D, *>, U> useLayerWithReturn(
        type: FrameworkStorageType, lambda: T.() -> U
    ): U {
        type.validate()

        val layer = localLayerCache[type]
            ?: throw RuntimeException("No layer found with ${type.name}")

        return (layer as T).let(lambda)
    }

    inline fun <reified T : FrameworkStorageLayer<*, D, *>> useLayer(
        type: FrameworkStorageType, lambda: T.() -> Unit
    ) {
        type.validate()

        localLayerCache[type]?.let {
            (it as T).let(lambda)
        }
    }

    fun loadOptimalCopy(
        identifier: UUID,
        ifAbsent: () -> D
    ): CompletableFuture<D> {
        val debugFrom = "${identifier}-${dataType.simpleName}"
        val start = System.currentTimeMillis()

        val extendedAbsent = {
            "Creating new copy".debug(debugFrom)
            ifAbsent.invoke()
        }

        val typeUsed = if (this.timestampField == null)
            FrameworkStorageType.MONGO else FrameworkStorageType.REDIS

        "Loading from ${typeUsed.name}...".debug(debugFrom)

        return load(
            identifier, typeUsed
        ).thenApply {
            var fetched = it

            if (fetched == null)
                "Couldn't find a copy in ${typeUsed.name}".debug(debugFrom)
            else
                "Found copy in ${typeUsed.name}".debug(debugFrom)

            if (
                this.timestampField != null &&
                fetched != null
            ) {
                val timestamp = this
                    .timestampField!!
                    .get(fetched)

                if (timestamp != null) {
                    "Found timestamp from copy".debug(debugFrom)

                    val difference =
                        System.currentTimeMillis() - timestamp as Long

                    val exceedsThreshold =
                        difference >= this.timestampThreshold

                    if (exceedsThreshold) {
                        "Timestamp exceeds threshold, retrieving from mongo".debug(debugFrom)

                        fetched = this.load(
                            identifier,
                            FrameworkStorageType.MONGO
                        ).join()
                    }
                }
            }

            val data = if (
                fetched == null &&
                this.timestampField != null
            ) {
                "Attempting to retrieve from MONGO".debug(debugFrom)

                this.load(
                    identifier,
                    FrameworkStorageType.MONGO
                ).join() ?: extendedAbsent.invoke()
            } else fetched ?: extendedAbsent.invoke()

            useLayer<CachedFrameworkStorageLayer<D>>(
                FrameworkStorageType.CACHE
            ) {
                this.saveSync(data)
            }

            "Completed process in ${System.currentTimeMillis() - start}ms".debug(debugFrom)

            return@thenApply data
        }
    }

    fun save(
        data: D,
        type: FrameworkStorageType = FrameworkStorageType.ALL
    ): CompletableFuture<Void> {
        var properType = type

        // updating the last-saved timestamp
        this.timestampField?.apply {
            set(data, System.currentTimeMillis())

            // we want a copy stoRED in
            // both MONGO & REDIS
            properType = FrameworkStorageType.ALL
        }

        val layer = localLayerCache[properType]

        return if (layer == null) {
            val status = CompletableFuture
                .runAsync {
                    localLayerCache.values.forEach { storageLayer ->
                        storageLayer.save(data).join()
                    }
                }

            status
        } else {
            layer.save(data)
        }
    }

    fun load(
        identifier: UUID,
        type: FrameworkStorageType
    ): CompletableFuture<D?> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.load(identifier)
    }

    fun delete(
        identifier: UUID,
        type: FrameworkStorageType
    ): CompletableFuture<Void> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.delete(identifier)
    }

    fun loadAll(
        type: FrameworkStorageType
    ): CompletableFuture<Map<UUID, D>> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.loadAll()
    }

    fun loadMultiple(
        type: FrameworkStorageType,
        vararg identifiers: UUID
    ): CompletableFuture<Map<UUID, D?>> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.loadMultiple(*identifiers)
    }

    fun saveMultiple(
        type: FrameworkStorageType,
        vararg objects: D
    ): CompletableFuture<Void> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.saveMultiple(*objects)
    }

    fun deleteMultiple(
        type: FrameworkStorageType,
        vararg identifiers: UUID
    ): CompletableFuture<Void> {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.deleteMultiple(*identifiers)
    }
}