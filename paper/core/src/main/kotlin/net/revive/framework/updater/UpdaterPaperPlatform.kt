package net.revive.framework.updater

import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import org.bukkit.Bukkit
import java.io.File

object UpdaterPaperPlatform : IUpdaterPlatform {

    override fun configure() {
        // bukkit file container
        UpdaterService.pluginContainer = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.NexusConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
            .apply {
                assets += "net.revive.framework:paper-core:Framework"
            }
    }
}
