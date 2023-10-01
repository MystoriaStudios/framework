package net.mystoria.framework.config

@Target(AnnotationTarget.CLASS)
annotation class JsonConfig(
    val fileName: String
)