package net.revive.framework.sender

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

class PaperFrameworkPlayer(player: Player) : AbstractFrameworkPlayer<Player>(player) {
    override val uniqueId: UUID = player.uniqueId

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }

    override fun sendMessages(vararg messages: Component) {
        messages.forEach { player.sendMessage(it) }
    }

    override fun playSound(sound: Sound) {
        player.playSound(sound)
    }
}