package net.revive.framework.orchestrator

import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.revive.framework.controller.FrameworkObjectController
import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.storage.storable.IStorable
import net.revive.framework.storage.type.FrameworkStorageType
import net.revive.framework.utils.listener
import java.util.*
import kotlin.reflect.KClass

abstract class ProfileOrchestrator<T : IStorable> {

    private lateinit var controller: FrameworkObjectController<T>

    fun find(player: Player): T? = controller.localCache()[player.uuid]
    fun find(uniqueId: UUID): T? = controller.localCache()[uniqueId]

    fun subscribe() {
        controller = FrameworkObjectControllerCache.create(type())

        listener<AsyncPlayerPreLoginEvent> {
            execute {
                controller
                    .loadOptimalCopy(
                        it.playerUuid
                    ) {
                        new(it.playerUuid)
                    }.join()

                this@ProfileOrchestrator.postLoad(it.playerUuid)
            }
        }

        listener<PlayerDisconnectEvent> {
            execute {
                controller.localCache().remove(it.player.uuid)
                    ?.let { cached ->
                        this@ProfileOrchestrator.controller.save(cached, FrameworkStorageType.MONGO)
                    }
            }
        }
    }

    open fun postLoad(uniqueId: UUID) {

    }

    abstract fun type(): KClass<T>
    abstract fun new(uniqueId: UUID): T
}
