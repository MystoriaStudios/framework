package net.mystoria.framework.deathmessage.damage

import org.bukkit.entity.EntityType
import java.util.*

abstract class MobAbstractDamage(
    damaged: UUID,
    damage: Double,
    val mobType: EntityType
) : AbstractDamage(
    damaged,
    damage
)
