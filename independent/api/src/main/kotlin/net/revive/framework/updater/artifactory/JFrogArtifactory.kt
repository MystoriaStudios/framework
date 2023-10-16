package net.revive.framework.updater.artifactory

import net.revive.framework.updater.artifactory.artifact.ArtifactMetadata
import net.revive.framework.updater.artifactory.artifact.component.ArtifactComponentMetadata
import net.revive.framework.updater.connection.UpdaterConnector
import net.revive.framework.updater.discovery.UpdaterDiscoveryService

object JFrogArtifactory {
    fun findComponentMetadata(
        path: String, repositoryOverride: String? = null
    ): ArtifactComponentMetadata {
        val assets = UpdaterDiscoveryService.discoverable

        val body = UpdaterConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return net.revive.framework.Framework.useWithReturn {
            it.serializer.deserialize(ArtifactComponentMetadata::class, body)
        }
    }

    fun findMetadata(
        path: String, repositoryOverride: String? = null
    ): ArtifactMetadata {
        val assets = UpdaterDiscoveryService.discoverable

        val body = UpdaterConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return net.revive.framework.Framework.useWithReturn {
            it.serializer.deserialize(ArtifactMetadata::class, body)
        }
    }
}
