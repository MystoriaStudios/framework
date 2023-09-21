package net.mystoria.framework.orchestrator

import me.lucko.helper.Events
import net.mystoria.framework.controller.FrameworkObjectController
import net.mystoria.framework.controller.FrameworkObjectControllerCache
import net.mystoria.framework.storage.storable.IStorable
import net.mystoria.framework.storage.type.FrameworkStorageType
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.reflect.KClass

abstract class ProfileOrchestrator<T : IStorable> {

    private lateinit var controller: FrameworkObjectController<T>

    fun find(player: Player): T? = controller.localCache()[player.uniqueId]
    fun find(uniqueId: UUID): T? = controller.localCache()[uniqueId]

    fun subscribe() {
        controller = FrameworkObjectControllerCache.create(type())

        Events.subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler {
                controller
                    .loadOptimalCopy(
                        it.uniqueId
                    ) {
                        new(it.uniqueId)
                    }.join()

                this.postLoad(it.uniqueId)
            }

        Events.subscribe(PlayerQuitEvent::class.java).handler {
            controller.localCache().remove(it.player.uniqueId)
                ?.let { cached ->
                    this.controller.save(cached, FrameworkStorageType.MONGO)
                }
        }
    }

    open fun postLoad(uniqueId: UUID)
    {

    }

    abstract fun type(): KClass<T>
    abstract fun new(uniqueId: UUID): T
}
