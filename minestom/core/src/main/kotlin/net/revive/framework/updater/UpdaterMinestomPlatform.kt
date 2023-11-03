package net.revive.framework.updater

import net.hollowcube.minestom.extensions.ExtensionBootstrap
import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService

object UpdaterMinestomPlatform : IUpdaterPlatform {
    override fun configure() {
        UpdaterService.pluginContainer = ExtensionBootstrap.getExtensionManager().extensionFolder

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.JFrogConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
    }
}