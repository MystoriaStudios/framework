package net.revive.framework.sender

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import java.util.UUID

interface FrameworkSender<T> {

    val uniqueId: UUID

    fun sendMessage(message: Component)
    fun sendMessages(vararg messages: Component)
    fun playSound(sound: Sound)
}