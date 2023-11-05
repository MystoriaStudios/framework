package net.revive.framework.maven

import javax.annotation.Nonnull


/**
 * Annotation to indicate the required libraries for a class.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MavenLibraries(@get:Nonnull vararg val value: MavenLibrary = [])
