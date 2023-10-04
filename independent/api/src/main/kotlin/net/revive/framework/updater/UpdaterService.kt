package net.revive.framework.updater

import net.revive.framework.Framework
import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import java.io.File

object UpdaterService {

    lateinit var pluginContainer: File
    lateinit var authentication: UpdaterAuthenticationService.MystoriaConnectionAuthenticationWrapper
    lateinit var discoverable: UpdaterDiscoveryService.DiscoverableAssets

    private lateinit var platform: IUpdaterPlatform

    fun configure(platform: IUpdaterPlatform) {
        net.revive.framework.Framework.use {
            this.platform = platform
            platform.configure()
            it.flavor.bind<UpdaterService>() to this
        }
    }

    fun reload() {
        platform.configure()
    }

    fun createLoginMessage(
        vararg updates: Pair<String, String>,
        theme: String = "&6",
    ): String
    {
        var loginMessage =
            "${theme}Plugin updates are available for: "

        for ((index, pair) in updates.withIndex())
        {
            if (
                updates.size != 1 &&
                index == updates.size - 1
            )
            {
                loginMessage += "and "
            }

            loginMessage += "&f${pair.first} &7(${pair.second})${theme}${
                if (index == updates.size - 1) "." else ", "
            }"
        }

        return loginMessage
    }
}