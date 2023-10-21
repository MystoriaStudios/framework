package net.revive.framework.sender

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

class MinestomFrameworkPlayer(p: Player) : AbstractFrameworkPlayer<Player>(p) {

    override val uniqueId: UUID = p.uuid

    override fun sendMessage(message: Component) {
        p.sendMessage(message)
    }

    override fun sendMessages(vararg messages: Component) {
        messages.forEach {
            p.sendMessage(it)
        }
    }
}