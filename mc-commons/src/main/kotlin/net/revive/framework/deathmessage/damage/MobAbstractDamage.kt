package net.revive.framework.deathmessage.damage

import java.util.*

abstract class MobAbstractDamage(
    damaged: UUID,
    damage: Double,
    val mobType: String
) : AbstractDamage(
    damaged,
    damage
)
