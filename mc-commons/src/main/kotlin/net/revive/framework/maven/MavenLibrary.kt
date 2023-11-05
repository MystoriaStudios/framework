package net.revive.framework.maven

import javax.annotation.Nonnull


/**
 * Annotation to indicate a required library for a class.
 */
@MustBeDocumented
@JvmRepeatable(MavenLibraries::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MavenLibrary(
    /**
     * The group id of the library
     *
     * @return the group id of the library
     */
    @get:Nonnull val groupId: String,
    /**
     * The artifact id of the library
     *
     * @return the artifact id of the library
     */
    @get:Nonnull val artifactId: String,
    /**
     * The version of the library
     *
     * @return the version of the library
     */
    @get:Nonnull val version: String,
    /**
     * The repo where the library can be obtained from
     *
     * @return the repo where the library can be obtained from
     */
    @get:Nonnull val repo: Repository = Repository(url = "https://repo1.maven.org/maven2")
)

