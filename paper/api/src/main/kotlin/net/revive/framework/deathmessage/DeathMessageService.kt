package net.revive.framework.deathmessage

import net.revive.framework.deathmessage.configuration.IDeathMessageConfiguration
import net.revive.framework.deathmessage.damage.AbstractDamage
import net.revive.framework.flavor.service.Service
import org.bukkit.entity.Player
import java.util.*


@Service
object DeathMessageService {

    var configuration: IDeathMessageConfiguration = IDeathMessageConfiguration.DEFAULT_CONFIGURATION
    private val damage: MutableMap<UUID, MutableList<AbstractDamage>> = mutableMapOf()

    fun getDamage(player: Player): MutableList<AbstractDamage> {
        return if (damage.containsKey(player.uniqueId)) damage[player.uniqueId]!! else mutableListOf()
    }

    fun addDamage(player: Player, addedDamage: AbstractDamage) {
        damage.putIfAbsent(player.uniqueId, ArrayList())
        val damageList: MutableList<AbstractDamage> = damage[player.uniqueId]!!
        while (damageList.size > 30) damageList.removeAt(0)
        damageList.add(addedDamage)
    }

    fun clearDamage(player: Player) = damage.remove(player.uniqueId)
}