package net.mystoria.framework.menu

import net.mystoria.framework.flavor.annotation.Inject
import net.mystoria.framework.flavor.service.Close
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

@Service
object MenuService {

    private lateinit var openedMenus: MutableMap<UUID, IMenu>

    lateinit var menuHandler: IMenuHandler

    @Configure
    fun configure() {
        openedMenus = mutableMapOf()
    }

    @Close
    fun close()
    {
        openedMenus.forEach { (t, u) ->
            Bukkit.getPlayer(t)?.let {
                u.onClose(it)
            }
        }
    }

    fun hasOpenedMenu(player: Player) = openedMenus.containsKey(player.uniqueId)
    fun getOpenedMenu(player: Player) = openedMenus[player.uniqueId]

    fun addOpenedMenu(player: Player, menu: IMenu)
    {
        openedMenus[player.uniqueId] = menu
    }

    fun removeOpenedMenu(player: Player) {
        openedMenus.remove(player.uniqueId)
    }
}