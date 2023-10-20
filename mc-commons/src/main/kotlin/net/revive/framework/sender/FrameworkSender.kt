package net.revive.framework.sender

import net.kyori.adventure.text.Component

interface FrameworkSender<T> {
    fun sendMessage(message: Component)
    fun sendMessages(vararg messages: Component)
}