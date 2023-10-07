package net.revive.framework.visibility

import net.revive.framework.visibility.override.IOverrideHandler
import net.revive.framework.visibility.override.OverrideResult
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * Represents a handler for managing player visibility within the server.
 */
interface IVisibilityHandler {

    /**
     * Registers a visibility provider with a specified identifier.
     *
     * @param identifier The unique identifier for the provider.
     * @param handler The visibility provider to register.
     */
    fun registerProvider(identifier: String, handler: IVisibilityProvider)

    /**
     * Registers an override handler with a specified identifier.
     *
     * @param identifier The unique identifier for the override.
     * @param handler The override handler to register.
     */
    fun registerOverride(identifier: String, handler: IOverrideHandler)

    /**
     * Updates the visibility settings for a specific player.
     *
     * @param player The player whose visibility settings are to be updated.
     */
    fun update(player: Player)

    /**
     * Updates the visibility of all players to a specific viewer.
     * This method is deprecated.
     *
     * @param viewer The player who will see (or not see) the updated players.
     */
    @Deprecated("")
    fun updateAllTo(viewer: Player)

    /**
     * Updates the visibility of a target player to all online players.
     * This method is deprecated.
     *
     * @param target The player whose visibility is to be updated.
     */
    @Deprecated("")
    fun updateToAll(target: Player)

    /**
     * Determines whether a target player should be treated as online with respect to a viewer.
     *
     * @param target The player being checked.
     * @param viewer The player for whom the check is being performed.
     * @return True if the target should be treated as online, false otherwise.
     */
    fun treatAsOnline(target: Player, viewer: Player): Boolean

    /**
     * Determines whether a viewer should see a target player.
     *
     * @param target The player being checked.
     * @param viewer The player for whom the check is being performed.
     * @return True if the viewer should see the target, false otherwise.
     */
    fun shouldSee(target: Player, viewer: Player): Boolean

    /**
     * Retrieves debug information regarding the visibility of a target player with respect to a viewer.
     *
     * @param target The player in question.
     * @param viewer The player for whom the information is being retrieved.
     * @return A list of strings containing debug information.
     */
    fun getDebugInfo(target: Player, viewer: Player): List<String>
}
