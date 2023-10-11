package net.revive.framework.annotation.custom

import kotlin.reflect.KClass

/**
 * @author Dash
 * @since 4/16/2022
 */
object CustomAnnotationProcessors {
    val processors =
        mutableMapOf<KClass<out Annotation>, (Any) -> Unit>()

    inline fun <reified T : Annotation> process(
        noinline lambda: (Any) -> Unit
    ) {
        processors[T::class] = lambda
    }
}
