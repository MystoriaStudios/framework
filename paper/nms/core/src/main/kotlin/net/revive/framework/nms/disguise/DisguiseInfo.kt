package net.revive.framework.nms.disguise

import java.util.*

/**
 * Represents the necessary information for player disguises in the game.
 *
 * @property uuid The UUID of the player for whom this disguise information pertains.
 * @property username The username of the player.
 * @property skinInfo The data representing the skin texture for the disguise.
 * @property skinSignature The Mojang-provided signature validating the skin data.
 */
open class DisguiseInfo(
    val uuid: UUID,
    val username: String,
    val skinInfo: String,
    val skinSignature: String
)