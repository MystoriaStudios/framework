package net.revive.framework.annotation.detail

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Export(
    val name: String
)
