package net.mystoria.framework.command

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.ExceptionHandler
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import net.mystoria.framework.Framework
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
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

        defaultExceptionHandler = ExceptionHandler { command, _, sender, _, throwable ->
            Framework.use {
                it.sentryService.log(throwable) { id ->
                    var message = "${ChatColor.RED}Whoops! we ran into an error whilst trying to do that. "
                    message += if (id != null) {
                        "Please report the following error code to a platform administrator ${ChatColor.YELLOW}$id"
                    } else "Please try again later."
                    sender.sendMessage(message)
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
}