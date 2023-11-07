package net.revive.framework.allocation

import net.revive.framework.FrameworkApp
import net.revive.framework.config.JsonConfig
import net.revive.framework.config.load
import net.revive.framework.config.save
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import kotlin.reflect.full.findAnnotation

@Service
object AllocationService {

    var usedPorts = mutableListOf<Int>()
    var config = AllocationConfig()

    @Configure
    fun configure() {
        FrameworkApp.use { app ->
            val annotation = config::class.findAnnotation<JsonConfig>() ?: throw RuntimeException("Unable to get annotation")

            config = try {
                app.load(annotation)
            } catch (_: Exception) {
                app.save<AllocationConfig>(annotation, config)
            }
            config.allocations = config.allocations.map {
                it.apply {
                    state = Allocation.State.FREE
                }
            }.toMutableList()
        }
    }

    @Close
    fun close() {
        FrameworkApp.use { app ->
            config = app.save(config::class.findAnnotation<JsonConfig>()!!, config)
        }
    }

    fun take() : Allocation? {
        return config.allocations.firstOrNull {
            it.port !in usedPorts
        }.apply {
            this?.let { mark(it.port) }
        }
    }

    fun mark(port: Int) {
        if (port !in usedPorts) usedPorts.add(port)
    }

    fun unmark(port: Int) {
        usedPorts.remove(port)
    }
}