package net.mystoria.framework.updater.discovery

import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import net.mystoria.framework.serializer.IAbstractTypeSerializable
import net.mystoria.framework.updater.UpdaterService

@Service(priority = 45)
object UpdaterDiscoveryService
{
    var discoverable = UpdaterService.discoverable

    @Configure
    fun configure()
    {
        discoverable = UpdaterService.discoverable
    }

    class DiscoverableAssets(
        val discoveryUrl: String = "https://artifactory.junglerealms.org",
        val discoveryRepositories: List<String> = listOf(
            "gradle-dev-local"
        ),
        val assets: MutableList<String> = mutableListOf(
            "net.mystoria.framework.vines:spigot:vines",
        )
    ) : IAbstractTypeSerializable
    {
        override fun getAbstractType() = DiscoverableAssets::class.java
    }
}
