package net.revive.framework.storage.impl

import net.revive.framework.connection.redis.AbstractFrameworkRedisConnection
import net.revive.framework.controller.FrameworkObjectController
import net.revive.framework.storage.FrameworkStorageLayer
import net.revive.framework.storage.storable.IStorable
import java.util.*
import kotlin.reflect.KClass

class RedisFrameworkStoreStorageLayer<D : IStorable>(
    connection: AbstractFrameworkRedisConnection,
    private val container: FrameworkObjectController<D>,
    private val dataType: KClass<D>
) : FrameworkStorageLayer<AbstractFrameworkRedisConnection, D, (D) -> Boolean>(connection) {
    private var section = "Framework:${dataType.simpleName}"

    /**
     * Allow a user to build their own
     * custom section with our Redis cache.
     *
     * All sections must start with `Framework:`
     */
    fun withCustomSection(
        section: StringBuilder.() -> Unit
    ) {
        val builder = StringBuilder()
            .append("Framework:")
            .apply(section)

        this.section = builder.toString()
    }

    override fun loadAllWithFilterSync(
        filter: (D) -> Boolean
    ): Map<UUID, D> {
        return loadAllSync().filter {
            filter.invoke(it.value)
        }
    }

    override fun loadWithFilterSync(filter: (D) -> Boolean): D? {
        return loadAllSync().filter {
            filter.invoke(it.value)
        }.values.firstOrNull()
    }

    override fun saveSync(data: D) {
        runSafely {
            connection.useResource {
                sync().hset(
                    section, data.identifier.toString(),
                    net.revive.framework.Framework.instance.serializer.serialize(data)
                )
            }
        }
    }

    override fun loadSync(identifier: UUID): D? {
        return runSafelyReturn {
            val serialized = connection.useResourceWithReturn {
                sync().hget(section, identifier.toString())
            } ?: return@runSafelyReturn null

            return@runSafelyReturn net.revive.framework.Framework.instance.serializer.deserialize(dataType, serialized)
        }
    }

    override fun loadAllSync(): Map<UUID, D> {
        return runSafelyReturn {
            val serialized = connection.useResourceWithReturn {
                sync().hgetall(section)
            } ?: return@runSafelyReturn mutableMapOf()

            val deserialized = mutableMapOf<UUID, D>()

            for (mutableEntry in serialized) {
                deserialized[UUID.fromString(mutableEntry.key)] =
                    net.revive.framework.Framework.instance.serializer.deserialize(dataType, mutableEntry.value)
            }

            return@runSafelyReturn deserialized
        }
    }

    override fun deleteSync(identifier: UUID) {
        runSafely {
            connection.useResource {
                sync().hdel(section, identifier.toString())
            }
        }
    }
}
