package net.mystoria.framework.storage.impl

import net.mystoria.framework.connection.cached.FrameworkCacheConnection
import net.mystoria.framework.storage.FrameworkStorageLayer
import net.mystoria.framework.storage.storable.IStorable
import java.util.*

class CachedFrameworkStorageLayer<D : IStorable> : FrameworkStorageLayer<FrameworkCacheConnection<UUID, D>, D, (D) -> Boolean>(
    FrameworkCacheConnection()
) {
    override fun saveSync(data: D) {
        connection.getConnection()[data.identifier] = data
    }

    override fun loadSync(identifier: UUID) = connection.getConnection()[identifier]
    override fun loadWithFilterSync(filter: (D) -> Boolean) = connection.getConnection().values.firstOrNull(filter)
    override fun loadAllSync(): Map<UUID, D> = connection.getConnection()
    override fun loadAllWithFilterSync(
        filter: (D) -> Boolean
    ): Map<UUID, D> {
        return connection.getConnection()
            .apply {
                val filtered = this.values
                    .filter(filter)

                mutableMapOf<UUID, D>()
                    .also { map ->
                        filtered.forEach { map[it.identifier] = it }
                    }
            }
    }

    override fun deleteSync(identifier: UUID) { connection.getConnection().remove(identifier) }
}