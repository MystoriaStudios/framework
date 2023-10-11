package net.revive.framework.menu

import net.revive.framework.annotation.Listeners
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Configure
import net.revive.framework.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Listeners
class FrameworkMenuUpdater : Runnable, Listener {

    @Inject
    lateinit var menuService: MenuService

    @Inject
    lateinit var menuHandler: IMenuHandler


    private val updateTimestamps: MutableMap<UUID, Long> = ConcurrentHashMap()

    @Configure
    fun configure() {
        Tasks.asyncTimer(this, 2L, 2L)
    }

    override fun run() {
        for ((uuid, openMenu) in menuService.getAllOpenedMenus().entries) {
            try {
                val player = Bukkit.getPlayer(uuid)
                if (player == null || !player.isOnline) {
                    menuService.removeOpenedMenu(uuid)
                    continue
                }

                if (openMenu.metaData.closed) {
                    continue
                }

                if (openMenu.autoUpdate) {
                    updateTimestamps.putIfAbsent(player.uniqueId, System.currentTimeMillis())

                    if (System.currentTimeMillis() - updateTimestamps[player.uniqueId]!! >= openMenu.autoUpdateInterval) {
                        updateTimestamps[player.uniqueId] = System.currentTimeMillis()
                        menuHandler.openMenu(player, openMenu)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        updateTimestamps.remove(event.player.uniqueId)
    }

}