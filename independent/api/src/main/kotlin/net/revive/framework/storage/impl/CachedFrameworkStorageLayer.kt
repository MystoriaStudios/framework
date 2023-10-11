package net.revive.framework.storage.impl

import net.revive.framework.connection.cached.FrameworkCacheConnection
import net.revive.framework.storage.FrameworkStorageLayer
import net.revive.framework.storage.storable.IStorable
import java.util.*

class CachedFrameworkStorageLayer<D : IStorable> :
    FrameworkStorageLayer<FrameworkCacheConnection<UUID, D>, D, (D) -> Boolean>(
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
                val filteRED = this.values
                    .filter(filter)

                mutableMapOf<UUID, D>()
                    .also { map ->
                        filteRED.forEach { map[it.identifier] = it }
                    }
            }
    }

    override fun deleteSync(identifier: UUID) {
        connection.getConnection().remove(identifier)
    }
}