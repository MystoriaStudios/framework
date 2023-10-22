package net.revive.framework.sender

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.command.ConsoleCommandSender

class PaperFrameworkConsole(sender: ConsoleCommandSender) : AbstractFrameworkConsole<ConsoleCommandSender>(sender) {
    override fun sendMessage(message: Component) {
        console.sendMessage(message)
    }

    override fun sendMessages(vararg messages: Component) {
        messages.forEach { console.sendMessage(it) }
    }

    override fun playSound(sound: Sound) {

    }
}