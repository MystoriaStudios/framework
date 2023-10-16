package net.revive.framework.nms.annotation

import net.revive.framework.nms.NMSVersion

/**
 * Annotation to mark classes as NMS handlers for a specific Minecraft version.
 *
 * This annotation assists in the reflection and automatic dependency injection
 * process to determine the appropriate NMS handler for the given server version.
 *
 * @property version The NMS version for which the annotated class acts as a handler.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NMSHandler(val version: NMSVersion)
