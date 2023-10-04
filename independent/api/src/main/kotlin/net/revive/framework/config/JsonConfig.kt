package net.revive.framework.config

@Target(AnnotationTarget.CLASS)
annotation class JsonConfig(
    val fileName: String
)