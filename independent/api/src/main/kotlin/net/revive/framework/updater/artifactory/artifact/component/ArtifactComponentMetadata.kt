package net.revive.framework.updater.artifactory.artifact.component

data class ArtifactComponentMetadata(
    val path: String,
    val uri: String,
    val mimeType: String,
    val downloadUri: String,
    val checksums: ArtifactComponentChecksums,
) {
    var version = "???"
}
