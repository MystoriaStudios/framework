package net.revive.framework.nms.disguise

/**
 * Interface for handling the internals of player disguises at the NMS (net.minecraft.server) level.
 */
interface INMSDisguiseHandler {

    /**
     * Retrieves the game profile of a player at the NMS level.
     *
     * @param player The player object at the NMS level.
     * @return The game profile of the specified player.
     */
    fun getGameProfile(player: Any): Any

    /**
     * Handles the internal process of disguising a player at the NMS level.
     *
     * @param player The player object at the NMS level.
     * @param disguiseInfo Information about the disguise to be applied.
     */
    fun handleDisguise(player: Any, disguiseInfo: DisguiseInfo)

    /**
     * Reloads the internal data of a player at the NMS level.
     * This can be used to reflect changes like a disguise.
     *
     * @param player The player object at the NMS level.
     */
    fun reloadPlayerInternals(player: Any)

    /**
     * Handles the internal process of und disguising a player at the NMS level.
     *
     * @param player The player object at the NMS level.
     * @param originalGameProfile The original game profile of the player before disguising.
     * @param disconnecting Flag indicating if the player is disconnecting.
     */
    fun handleUnDisguiseInternal(player: Any, originalGameProfile: Any, disconnecting: Boolean)
}
