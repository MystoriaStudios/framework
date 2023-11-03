package net.revive.framework.node

import net.revive.framework.storage.storable.IStorable
import java.util.*

class Node(
    val name: String,
    val href: String,
    val organization: String,
    val state: State,
    val pushedAt: Long = System.currentTimeMillis(),
    override val identifier: UUID = UUID.randomUUID()
) : IStorable {
    enum class State {
        BOOTING,
        SETUP,
        ONLINE,
        OFFLINE
    }
}