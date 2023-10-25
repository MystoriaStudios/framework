package net.revive.framework.state

import net.revive.framework.sender.AbstractFrameworkPlayer

abstract class State {
    fun tick(player: AbstractFrameworkPlayer<*>) {

    }
}