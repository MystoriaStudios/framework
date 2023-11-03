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
        }
    }

    @Close
    fun close() {
        FrameworkApp.use { app ->
            config = app.save(config::class.findAnnotation<JsonConfig>()!!, config)
        }
    }
}