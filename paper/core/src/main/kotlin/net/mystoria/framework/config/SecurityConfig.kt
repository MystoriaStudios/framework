package net.mystoria.framework.config

@JsonConfig("security.json")
class SecurityConfig(
    var interactionProtection: Boolean = false
)