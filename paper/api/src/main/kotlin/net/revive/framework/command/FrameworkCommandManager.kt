@file:Suppress("DEPRECATION")

package net.revive.framework.command

import co.aikar.commands.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.constants.Tailwind
import net.revive.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender
import java.util.regex.Pattern

class FrameworkCommandManager(
    plugin: ExtendedKotlinPlugin
) : PaperCommandManager(plugin) {
    init {
        enableUnstableAPI("help")
        enableUnstableAPI("brigadier")

        listOf<MessageType>(
            MessageType.HELP,
            MessageType.ERROR,
            MessageType.INFO,
            MessageType.SYNTAX
        ).forEach {
            for (i in 1 until 3) getFormat(it).setColor(i, ChatColor.RED)
        }

        defaultExceptionHandler = ExceptionHandler { _, _, sender, _, throwable ->
            net.revive.framework.Framework.use {
                it.sentryService.log(throwable) { id ->
                    val message = Component.text("Whoops! we ran into an error whilst trying to do that. ")
                        .color(TextColor.fromHexString(Tailwind.RED_600))
                    message.append(
                        Component.text(
                            if (id != null) {
                                "Please report the following error code to a platform administrator"
                            } else "Please try again later."
                        )
                    ).color(TextColor.fromHexString(Tailwind.RED_600))
                    if (id != null) {
                        message.append(
                            Component
                                .text("$id")
                                .color(TextColor.fromHexString(Tailwind.ORANGE_400))
                                .clickEvent(ClickEvent.openUrl("$id"))
                        )
                    }

                    (Bukkit.getPlayer(sender.uniqueId)
                        ?: if (!sender.isPlayer) Bukkit.getConsoleSender() else null)?.sendMessage(message)
                }
            }

            return@ExceptionHandler true
        }

        commandConditions.addCondition(String::class.java, "validate") { c, _, value ->
            if (value == null) return@addCondition

            if (c.hasConfig("min") && c.getConfigValue("min", 3) > value.length) {
                throw ConditionFailedException("Minimum length must be " + c.getConfigValue("min", 0))
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 16) < value.length) {
                throw ConditionFailedException("Maximum length must be " + c.getConfigValue("max", 3))
            }
            if (c.hasConfig("regex") && Pattern.compile(c.getConfigValue("regex", "")).matcher(value).find()) {
                throw ConditionFailedException("The value provided cannot be used in this context.")
            }
        }



        commandContexts.registerIssuerOnlyContext(ConsoleCommandSender::class.java) { c ->
            if (c.sender !is ConsoleCommandSender) throw ConditionFailedException("Only console may perform this command.")

            return@registerIssuerOnlyContext c.sender as ConsoleCommandSender
        }
    }

    override fun hasPermission(issuer: CommandIssuer, permissions: MutableSet<String>?): Boolean {
        if (!issuer.isPlayer) return true
        val player = Bukkit.getPlayer(issuer.uniqueId) ?: return false

        val permission = player.effectivePermissions.find {
            permissions?.contains(it.permission) == true
        }?.permission ?: ""

        return net.revive.framework.Framework.useWithReturn {
            it.permissionProvider.evaluate(issuer.uniqueId, permission)
            it.permissionProvider.hasPermission(issuer.uniqueId, permission)
        }
    }
}