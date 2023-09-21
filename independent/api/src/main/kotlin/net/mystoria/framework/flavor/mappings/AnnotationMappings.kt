package net.mystoria.framework.flavor.mappings

import net.mystoria.framework.flavor.annotation.Named

/**
 * @author Dash
 * @since 9/14/2022
 */
object AnnotationMappings
{
    private val mappings = mutableMapOf(
        AnnotationType.Inject to listOf(
            net.mystoria.framework.flavor.annotation.Inject::class
        ),
        AnnotationType.Named to listOf(
            Named::class
        ),
        AnnotationType.Extract to listOf(
            net.mystoria.framework.flavor.annotation.Extract::class
        ),
        AnnotationType.PostConstruct to listOf(
            net.mystoria.framework.flavor.service.Configure::class
        ),
        AnnotationType.PREDestroy to listOf(
            net.mystoria.framework.flavor.service.Close::class
        )
    )

    fun matchesAny(
        type: AnnotationType,
        annotations: Array<Annotation>
    ): Boolean
    {
        val mapping = this.mappings[type]!!
        return annotations.any { it.annotationClass in mapping }
    }
}
