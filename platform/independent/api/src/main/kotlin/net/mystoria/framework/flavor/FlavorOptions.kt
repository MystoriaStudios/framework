package net.mystoria.framework.flavor

import java.util.logging.Logger

/**
 * @author Dash
 * @since 1/2/2022
 */
data class FlavorOptions
@JvmOverloads
constructor(
    val logger: Logger = Logger.getAnonymousLogger(),
    val `package`: String? = null
)
