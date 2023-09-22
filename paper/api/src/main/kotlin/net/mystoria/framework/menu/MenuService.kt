package net.mystoria.framework.menu

import net.mystoria.framework.flavor.service.Close
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service class for managing player menus and opened menus.
 */
@Service
object MenuService {

    // A mutable map to store opened menus associated with player UUIDs.
    private lateinit var openedMenus: MutableMap<UUID, IMenu>

    // A mutable map to store button click cooldowns associated with player UUIDS.
    lateinit var clickCooldown: MutableMap<UUID, Long>

    // A reference to the menu handler used by the service.
    lateinit var menuHandler: IMenuHandler

    /**
     * Initializes the `openedMenus` map when configuring the service.
     */
    @Configure
    fun configure() {
        openedMenus = mutableMapOf()
        clickCooldown = ConcurrentHashMap()
    }

    /**
     * Closes all opened menus and invokes the `onClose` method for each player.
     */
    @Close
    fun close() {
        openedMenus.forEach { (uuid, menu) ->
            Bukkit.getPlayer(uuid)?.let {
                menu.onClose(it)
            }
        }
    }

    /**
     * Checks if a player has an opened menu.
     *
     * @param player The player to check for an opened menu.
     * @return `true` if the player has an opened menu, `false` otherwise.
     */
    fun hasOpenedMenu(player: Player) = openedMenus.containsKey(player.uniqueId)

    /**
     * Retrieves the opened menu associated with a player.
     *
     * @param player The player for whom the opened menu is retrieved.
     * @return The opened menu associated with the player, or null if none is found.
     */
    fun getOpenedMenu(player: Player) = openedMenus[player.uniqueId]

    /**
     * Retrieves all opened menus associated with players.
     *
     * @return A map containing all opened menus associated with players, where the player's uniqueId is the key.
     */
    fun getAllOpenedMenus() = openedMenus

    /**
     * Adds an opened menu for a player.
     *
     * @param player The player for whom the menu is opened.
     * @param menu The menu to be added as an opened menu for the player.
     */
    fun addOpenedMenu(player: Player, menu: IMenu) {
        openedMenus[player.uniqueId] = menu
    }

    /**
     * Removes an opened menu associated with a player.
     *
     * @param player The player for whom the opened menu is removed.
     */
    fun removeOpenedMenu(player: Player) {
        openedMenus.remove(player.uniqueId)
    }

    /**
     * Removes an opened menu associated with a uuid.
     *
     * @param player The UUID of the player for whom the opened menu is removed.
     */
    fun removeOpenedMenu(player: UUID) {
        openedMenus.remove(player)
    }
}