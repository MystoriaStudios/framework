package net.revive.framework.event

import co.aikar.commands.ConditionFailedException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.constants.Tailwind
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

inline fun event(receiver: CommandSender = Bukkit.getConsoleSender(), lambda: () -> Unit) {
    try {
        lambda.invoke()
    } catch (failed: ConditionFailedException) {
        receiver.sendMessage(Component.text(failed.message ?: failed.stackTraceToString()).color(TextColor.fromHexString(Tailwind.RED_400)))
        if (receiver is ConsoleCommandSender) failed.printStackTrace()
    }
}