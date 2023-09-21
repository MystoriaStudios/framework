package net.mystoria.framework.updater

import net.mystoria.framework.updater.authentication.UpdaterAuthenticationService
import net.mystoria.framework.updater.discovery.UpdaterDiscoveryService
import org.bukkit.Bukkit
import java.io.File

object UpdaterPaperPlatform : IUpdaterPlatform {

    override fun configure()
    {
        // bukkit file container
        UpdaterService.pluginContainer = File(
            Bukkit.getWorldContainer(), "plugins"
        )

        // configuration
        UpdaterService.authentication = UpdaterAuthenticationService.JungleConnectionAuthenticationWrapper()
        UpdaterService.discoverable = UpdaterDiscoveryService.DiscoverableAssets()
            .apply {
                assets += "net.mystoria.framework:paper-core:Framework"
            }
    }
}
