package net.mystoria.framework.flavor.annotation

/**
 * @author Dash
 * @since 1/2/2022
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject
