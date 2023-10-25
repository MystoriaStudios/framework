package net.revive.framework.instance

import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.storage.type.FrameworkStorageType
import java.util.*

@Service
object InstanceService {

    val controller = FrameworkObjectControllerCache.create<Instance>()
    lateinit var local: Instance

    @Configure
    fun configure() {
        controller.setupCache(FrameworkStorageType.REDIS)
    }

    fun byId(id: String) : Instance? = controller.cache().loadSync(UUID.nameUUIDFromBytes(id.toByteArray()))
}