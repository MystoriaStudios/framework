package net.revive.framework.updater.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import kong.unirest.GetRequest
import kong.unirest.Unirest
import kong.unirest.UnirestInstance
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import net.revive.framework.updater.repository.Repositories
import net.revive.framework.updater.repository.component.ComponentWrapper
import net.revive.framework.updater.repository.component.asset.ComponentAssetWrapper
import net.revive.framework.updater.repository.repositories.ComponentPaginatedRepositoryWrapper
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import kotlin.properties.Delegates

/**
 * @author Dash
 * @since 1/21/2022
 */
object NexusUpdaterConnector
{
    @Inject
    lateinit var shared: UpdaterService

    private var unirest by Delegates
        .notNull<UnirestInstance>()

    @Configure
    fun configure()
    {
        unirest = Unirest.primaryInstance()

        usePendingUpdates { asset, _, name ->
            shared.logger.warning("$name is outdated! Restart your server to update this plugin! (${asset.maven2?.version})")
        }
    }

    @Close
    fun close()
    {
        shared.reload()

        applyPendingUpdates()
    }

    private fun applyPendingUpdates()
    {
        usePendingUpdatesSync { asset, file, name ->
            val start = System.currentTimeMillis()
            shared.logger.info("Downloading the latest $name (v${asset.maven2?.version}) archive...")

            try
            {
                request(asset.downloadUrl).asObject {
                    FileUtils.copyInputStreamToFile(
                        it.content, file
                    )
                }

                shared.logger.warning(
                    "Successfully updated $name (took ${
                        System.currentTimeMillis() - start
                    }ms)!"
                )
            } catch (exception: Exception)
            {
                shared.logger.log(
                    Level.SEVERE,
                    "Oops, seems like an exception was thrown during the update process.",
                    exception
                )
            }
        }
    }

    private fun useTrackedAssets(
        components: ComponentPaginatedRepositoryWrapper? = null,
        lambda: (String, ComponentAssetWrapper, ComponentWrapper) -> Unit
    )
    {
        val request = components ?: Repositories.components()
        val discovered = mutableListOf<String>()

        for (component in request.items.asReversed())
        {
            // Checks if this asset should be updated
            val discoverable = UpdaterDiscoveryService
                .discoverable.assets
                .firstOrNull {
                    it.split(":")[0] == component.group &&
                            it.split(":")[1] == component.name
                }
                ?: continue

            if (discovered.contains(discoverable))
                continue

            // Find the jar archive asset we need
            val asset = component.assets
                .firstOrNull {
                    it.maven2 != null &&
                            it.maven2.extension == "jar"
                }
                ?: continue

            lambda.invoke(discoverable, asset, component)
            discovered.add(discoverable)
        }
    }

    private fun usePendingUpdatesSync(
        lambda: (ComponentAssetWrapper, File, String) -> Unit
    )
    {
        val pluginDirectory = File(
            shared.pluginContainer, "plugins"
        )

        useTrackedAssets { discoverable, asset, _ ->
            val fileToOverride = discoverable
                .split(":")[2]

            val jarFile = "$fileToOverride.jar"

            val file = pluginDirectory.listFiles()!!
                .firstOrNull {
                    it.name.equals(jarFile, true)
                }

            if (file != null)
            {
                // checking if the hashes are the same, so we can save some time
                // and avoid updating already up-to-date archives
                val md5Hash = Files
                    .asByteSource(file)
                    .hash(Hashing.md5())
                    .toString()

                if (md5Hash != asset.checksum.md5)
                {
                    lambda.invoke(asset, file, fileToOverride)
                }
            }
        }
    }

    fun usePendingUpdates(
        lambda: (ComponentAssetWrapper, File, String) -> Unit
    )
    {
        CompletableFuture.runAsync {
            usePendingUpdatesSync(lambda)
        }
    }

    fun request(request: String): GetRequest
    {
        val wrapper = UpdaterAuthenticationService.getWrapper() as UpdaterAuthenticationService.NexusConnectionAuthenticationWrapper

        return unirest.get(request)
            .basicAuth(wrapper.username, wrapper.password)
            .connectTimeout(1000)
    }
}
