package net.revive.framework.config

@JsonConfig("security.json")
class SecurityConfig(
    var interactionProtection: Boolean = false
)