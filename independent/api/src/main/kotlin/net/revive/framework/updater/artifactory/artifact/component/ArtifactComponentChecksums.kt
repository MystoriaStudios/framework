package net.revive.framework.updater.artifactory.artifact.component

data class ArtifactComponentChecksums(
    val sha1: String,
    val md5: String,
    val sha256: String
)
