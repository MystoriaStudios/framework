package net.revive.framework.updater.connection

import com.google.common.hash.Hashing
import com.google.common.io.Files
import kong.unirest.GetRequest
import kong.unirest.Unirest
import net.revive.framework.flavor.service.Close
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.updater.UpdaterService
import net.revive.framework.updater.artifactory.JFrogArtifactory
import net.revive.framework.updater.artifactory.artifact.ArtifactMetadata
import net.revive.framework.updater.artifactory.artifact.component.ArtifactComponentMetadata
import net.revive.framework.updater.authentication.UpdaterAuthenticationService
import net.revive.framework.updater.discovery.UpdaterDiscoveryService
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

object JFrogUpdaterConnector {

    @Configure
    fun configure() {
        net.revive.framework.Framework.use {
            usePendingUpdates { asset, _, name ->
                it.log("Updater", "$name is outdated! Restart your server to update this plugin! (${asset.version})")
            }
        }
    }

    @Close
    fun close() {
        UpdaterService.reload()
        applyPendingUpdates()
    }

    fun applyPendingUpdates() {
        net.revive.framework.Framework.use {
            usePendingUpdatesSync { asset, file, name ->
                val start = System.currentTimeMillis()
                it.log("Updater", "Downloading the latest $name (v${asset.version}) archive...")

                try {
                    file.delete()
                    request(asset.downloadUri)
                        .asObject {
                            FileUtils.copyInputStreamToFile(
                                it.content, file
                            )
                        }

                    it.log("Updater", "Successfully updated $name (took ${System.currentTimeMillis() - start}ms)!")
                } catch (exception: Exception) {
                    it.sentryService.log(exception) { id ->
                        it.severe(
                            "Updater",
                            "Oops, seems like there was an exception thrown during the update process. Check it out on sentry @ $id"
                        )
                    }
                }
            }
        }
    }

    private fun useTrackedAssets(
        lambda: (String, ArtifactMetadata, ArtifactComponentMetadata) -> Unit
    ) {
        val assets = UpdaterDiscoveryService
            .discoverable.assets.toMutableSet()

        for (component in assets) {
            val composite = component
                .split(":")

            val composed = "${
                composite[0].replace(".", "/")
            }/${composite[1]}"

            val metadata = JFrogArtifactory
                .findMetadata(
                    composed,
                    composite.getOrNull(3)
                )

            val mostRecent =
                metadata.mostRecent()
                    ?: continue

            val latestVersion = mostRecent.uri
                .replace("/", "")

            val mostRecentMeta = JFrogArtifactory
                .findMetadata(
                    "$composed${mostRecent.uri}",
                    composite.getOrNull(3)
                )

            val archive = mostRecentMeta
                .archive(
                    composite.getOrNull(3)
                )
            archive.version = latestVersion

            lambda.invoke(
                component, mostRecentMeta, archive
            )
        }
    }

    private fun usePendingUpdatesSync(
        lambda: (ArtifactComponentMetadata, File, String) -> Unit
    ) {
        useTrackedAssets { discoverable, _, component ->
            val fileToOverride = discoverable
                .split(":")[2]

            val jarFile = "$fileToOverride.jar"

            val file = UpdaterService.pluginContainer
                .listFiles()!!
                .firstOrNull {
                    it.name.equals(jarFile, true)
                }
                ?: return@useTrackedAssets

            // checking if the hashes are the same, so we can save some time
            // and avoid updating already up-to-date archives
            val md5Hash = Files
                .asByteSource(file)
                .hash(Hashing.sha256())
                .toString()

            if (md5Hash != component.checksums.sha256) {
                lambda.invoke(
                    component, file, fileToOverride
                )
            }
        }
    }

    fun usePendingUpdates(
        lambda: (ArtifactComponentMetadata, File, String) -> Unit
    ): CompletableFuture<Void> {
        return CompletableFuture
            .runAsync {
                usePendingUpdatesSync(lambda)
            }
            .exceptionally { throwable ->
                net.revive.framework.Framework.use {
                    it.sentryService.log(throwable) { id ->
                        it.severe("Updater", "Exception thrown during pending update task check it out on sentry @ $id")
                    }
                }
                return@exceptionally null
            }
    }

    fun request(request: String): GetRequest {
        val wrapper = UpdaterAuthenticationService.getWrapper() as UpdaterAuthenticationService.JFrogConnectionAuthenticationWrapper

        return Unirest.get(request)
            .header("X-JFrog-Art-Api", wrapper.apiKey)
            .connectTimeout(1000)
    }
}
