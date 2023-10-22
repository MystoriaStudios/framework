package net.revive.framework.utils

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import kotlin.reflect.KClass

class EventListenerBuilder<T : Event>(private val eventClass: KClass<T>) {
    var filter: (T) -> Boolean = { true }
    var execute: (T) -> Unit = {}

    infix fun filter(block: (T) -> Boolean) {
        filter = block
    }

    infix fun execute(block: (T) -> Unit) {
        execute = block
    }

    operator fun invoke() {
        MinecraftServer.getGlobalEventHandler().addListener(eventClass.java) { event ->
            if (filter(event)) {
                execute(event)
            }
        }
    }
}

inline fun <reified T : Event> listener(noinline block: EventListenerBuilder<T>.() -> Unit) {
    val builder = EventListenerBuilder(T::class)
    builder.block()
    builder.invoke()
    //AstraeServer.logger.info("Created listener ${T::class.simpleName}")
}
