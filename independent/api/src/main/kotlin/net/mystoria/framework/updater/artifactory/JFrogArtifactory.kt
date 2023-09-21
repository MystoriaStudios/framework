package net.mystoria.framework.updater.artifactory

import net.mystoria.framework.Framework
import net.mystoria.framework.updater.artifactory.artifact.ArtifactMetadata
import net.mystoria.framework.updater.artifactory.artifact.component.ArtifactComponentMetadata
import net.mystoria.framework.updater.connection.UpdaterConnector
import net.mystoria.framework.updater.discovery.UpdaterDiscoveryService

object JFrogArtifactory
{
    fun findComponentMetadata(
        path: String, repositoryOverride: String? = null
    ): ArtifactComponentMetadata
    {
        val assets = UpdaterDiscoveryService.discoverable

        val body = UpdaterConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return Framework.useWithReturn {
            it.serializer.deserialize(ArtifactComponentMetadata::class, body)
        }
    }

    fun findMetadata(
        path: String, repositoryOverride: String? = null
    ): ArtifactMetadata
    {
        val assets = UpdaterDiscoveryService.discoverable

        val body = UpdaterConnector
            .request(
                "${assets.discoveryUrl}/artifactory/api/storage/${
                    repositoryOverride ?: assets.discoveryRepositories.first()
                }/$path"
            )
            .asString().body

        return Framework.useWithReturn {
            it.serializer.deserialize(ArtifactMetadata::class , body)
        }
    }
}
