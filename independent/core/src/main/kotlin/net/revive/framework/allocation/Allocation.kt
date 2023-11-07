package net.revive.framework.allocation

class Allocation(
    var bindAddress: String,
    var port: Int,
    var state: State = State.FREE
) {
    enum class State {
        IN_USE,
        FREE
    }
            /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "$bindAddress:$port"
    }
}