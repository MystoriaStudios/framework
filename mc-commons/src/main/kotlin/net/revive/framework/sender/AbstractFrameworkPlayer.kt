package net.revive.framework.sender

import net.revive.framework.state.State

typealias FrameworkPlayer = AbstractFrameworkPlayer<*>

abstract class AbstractFrameworkPlayer<P>(val player: P) : FrameworkSender<P> {
    val states: MutableList<State> = mutableListOf()

    infix fun has(state: State) = states.any {
        it.javaClass == state.javaClass
    }

    fun state(state: State, use: Boolean? = null) {
        if (use ?: (this has state)) {
            states.removeIf {
                it.javaClass == state.javaClass
            }
        } else states.add(state)
    }
}