package net.revive.framework.updater

import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import java.io.File

object UpdaterIndependentPlatform : IUpdaterPlatform {

    override fun configure() {
        UpdaterService.pluginContainer = File("modules")

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.MystoriaConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
            .apply {
                assets += "net.revive.nebula:nebula-independent:Nebula"
            }
    }
}