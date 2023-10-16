package net.revive.framework.updater.discovery

import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.serializer.IAbstractTypeSerializable
import net.revive.framework.updater.UpdaterService

@Service(priority = 45)
object UpdaterDiscoveryService {
    var discoverable = UpdaterService.discoverable

    @Configure
    fun configure() {
        discoverable = UpdaterService.discoverable
    }

    class DiscoverableAssets(
        val discoveryUrl: String = "https://artifactory.junglerealms.org",
        val discoveryRepositories: List<String> = listOf(
            "gradle-dev-local"
        ),
        val assets: MutableList<String> = mutableListOf(

        )
    ) : IAbstractTypeSerializable {
        override fun getAbstractType() = DiscoverableAssets::class.java
    }
}
