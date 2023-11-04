package net.revive.framework.allocation

class Allocation(
    var bindAddress: String,
    var port: Int
) {
    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "$bindAddress:$port"
    }
}