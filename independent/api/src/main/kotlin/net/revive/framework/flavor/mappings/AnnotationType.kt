package net.revive.framework.flavor.mappings

/**
 * @author Dash
 * @since 9/14/2022
 */
enum class AnnotationType {
    // We really don't need @Qualifier and @Singleton due to how
    // our binder configuration works and how Kotlin objects work.
    Inject, Named, Extract, PreDestroy, PostConstruct
}
