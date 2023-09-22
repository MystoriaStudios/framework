package net.mystoria.framework.updater

import net.mystoria.framework.updater.authentication.UpdaterAuthenticationService
import net.mystoria.framework.updater.discovery.UpdaterDiscoveryService
import java.io.File

object UpdaterIndependentPlatform : IUpdaterPlatform {

    override fun configure() {
        UpdaterService.pluginContainer = File("modules")

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.JungleConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
            .apply {
                assets += "net.mystoria.mebula:nebula-independent:Nebula"
            }
    }
}