package net.revive.framework.sender

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

class MinestomFrameworkPlayer(p: Player) : AbstractFrameworkPlayer<Player>(p) {

    override val uniqueId: UUID = p.uuid

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }

    override fun sendMessages(vararg messages: Component) {
        messages.forEach {
            player.sendMessage(it)
        }
    }

    override fun playSound(sound: Sound) {
        player.playSound(sound)
    }
}