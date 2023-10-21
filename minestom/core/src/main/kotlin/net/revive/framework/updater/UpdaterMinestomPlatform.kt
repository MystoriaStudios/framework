package net.revive.framework.updater

import net.hollowcube.minestom.extensions.ExtensionBootstrap
import net.minestom.server.extensions.ExtensionManager
import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import java.io.File

object UpdaterMinestomPlatform : IUpdaterPlatform {
    override fun configure() {
        UpdaterService.pluginContainer = ExtensionBootstrap.getExtensionManager().extensionFolder

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.MystoriaConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
    }
}