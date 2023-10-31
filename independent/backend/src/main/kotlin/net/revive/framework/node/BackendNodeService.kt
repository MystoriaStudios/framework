package net.revive.framework.node

import net.revive.framework.controller.FrameworkObjectControllerCache
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service

@Service
object BackendNodeService {

    val controller = FrameworkObjectControllerCache.create<Node>()

    @Configure
    fun configure() {

    }
}