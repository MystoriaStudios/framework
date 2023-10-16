package net.revive.framework.disguise

import net.revive.framework.nms.disguise.DisguiseInfo
import org.bukkit.entity.Player

/**
 * Interface for handling player disguises.
 */
interface IDisguiseHandler {

    /**
     * Checks if a player is currently disguised.
     *
     * @param player The player to check.
     * @return True if the player is disguised, false otherwise.
     */
    fun isDisguised(player: Player): Boolean

    /**
     * Applies a disguise to a player.
     *
     * This method handles the internal process of disguising, without
     * triggering external messages or effects.
     *
     * @param player The player to be disguised.
     * @param disguiseInfo Information about the disguise to be applied.
     * @param callback An optional callback to be executed after disguising is complete.
     */
    fun disguise(player: Player, disguiseInfo: DisguiseInfo, callback: () -> Unit = {})

    /**
     * Removes a disguise from a player.
     *
     * This method handles the internal process of und disguising, without
     * triggering external messages or effects.
     *
     * @param player The player from whom the disguise is to be removed.
     * @param disconnecting Flag indicating if the player is disconnecting.
     * @param callback A callback to be executed after und disguising is complete.
     */
    fun unDisguise(player: Player, disconnecting: Boolean, callback: () -> Unit)
}