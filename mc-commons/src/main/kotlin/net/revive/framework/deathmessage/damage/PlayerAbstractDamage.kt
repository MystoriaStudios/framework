package net.revive.framework.deathmessage.damage

import java.util.*

abstract class PlayerAbstractDamage(
    damaged: UUID,
    damage: Double,
    val damager: UUID
) : AbstractDamage(
    damaged,
    damage
)
