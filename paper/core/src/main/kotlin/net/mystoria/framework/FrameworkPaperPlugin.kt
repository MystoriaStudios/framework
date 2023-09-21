package net.mystoria.framework

import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ap.Plugin
import net.mystoria.framework.annotation.container.ContainerEnable
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import net.mystoria.framework.updater.UpdaterPaperPlatform
import net.mystoria.framework.updater.UpdaterService

@Plugin(
    name = "Framework",
    version = "!{VERSION}",
    authors = ["Mystoria Studios"],
    website = "https://mystoria.net/",
)
@HelperImplementationPlugin
class FrameworkPaperPlugin : ExtendedKotlinPlugin() {

    @ContainerEnable
    fun containerEnable() {
        Framework.use {
            it.flavor = flavor()
        }

        UpdaterService.configure(UpdaterPaperPlatform)
        // bind the menu to the impleemnbtation here O,
    }
}