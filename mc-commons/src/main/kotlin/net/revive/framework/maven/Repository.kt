package net.revive.framework.maven

import javax.annotation.Nonnull


/**
 * Represents a maven repository.
 */
@MustBeDocumented
@Target(AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Repository(
    /**
     * Gets the base url of the repository.
     *
     * @return the base url of the repository
     */
    @get:Nonnull val url: String
)

