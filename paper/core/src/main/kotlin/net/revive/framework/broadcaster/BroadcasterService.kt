package net.revive.framework.broadcaster

import net.revive.framework.broadcaster.config.BroadcastConfig
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.utils.Tasks
import org.bukkit.Bukkit

@Service
object BroadcasterService {

    @Inject lateinit var config: BroadcastConfig
    var index = 0

    @Configure
    fun configure() {
        if (config.enabled) {
            Tasks.asyncTimer(config.delay * 20L, config.delay * 20L) {
                config.messages[index].forEach(Bukkit::broadcast)

                index++
                if (index > config.messages.size) index = 0
            }
        }
    }
}