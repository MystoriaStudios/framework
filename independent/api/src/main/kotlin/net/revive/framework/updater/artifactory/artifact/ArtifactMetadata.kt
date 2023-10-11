package net.revive.framework.updater.artifactory.artifact

import net.revive.framework.updater.artifactory.JFrogArtifactory
import net.revive.framework.updater.artifactory.artifact.component.ArtifactComponentMetadata

data class ArtifactMetadata(
    val path: String,
    val children: List<ArtifactChild>?
) {
    fun mostRecent() = children
        ?.lastOrNull { it.folder }

    fun archive(
        repositoryOverride: String? = null
    ): ArtifactComponentMetadata {
        val path = "$path${
            this.children!!
                .first {
                    it.uri.endsWith(".jar")
                }
                .uri
        }"

        return JFrogArtifactory
            .findComponentMetadata(
                path, repositoryOverride
            )
    }
}
