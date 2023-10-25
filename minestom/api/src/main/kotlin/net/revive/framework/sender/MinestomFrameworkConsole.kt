package net.revive.framework.sender

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.command.ConsoleSender

object MinestomFrameworkConsole :
    AbstractFrameworkConsole<ConsoleSender>(MinecraftServer.getCommandManager().consoleSender) {
    override fun sendMessage(message: Component) {
        console.sendMessage(message)
    }

    override fun sendMessages(vararg messages: Component) {
        messages.forEach {
            console.sendMessage(it)
        }
    }

    override fun playSound(sound: Sound) {
    }
}