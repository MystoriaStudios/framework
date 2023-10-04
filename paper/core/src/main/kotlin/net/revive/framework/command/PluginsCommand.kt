package net.revive.framework.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.revive.framework.PaperFramework
import net.revive.framework.annotation.command.AutoRegister
import net.revive.framework.constants.Tailwind
import net.revive.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.SimplePluginManager

@CommandAlias("plugins|pl|modules|module")
@AutoRegister
object PluginsCommand : FrameworkCommand() {

    @Default
    fun execute(sender: CommandSender) {
        sender.sendMessage("Displaying Server Software")
        sender.sendMessage("Version: ${Bukkit.getServer().version}")
        sender.sendMessage("Bukkit Release: ${Bukkit.getServer().bukkitVersion}")
        sender.sendMessage("Minecraft Version: ${Bukkit.getServer().minecraftVersion}")
        val mystoriaPlugins = PaperFramework.registeredKotlinPlugins
        val mystoriaComponent = Component
            .text("Revive: ")
            .color(TextColor.fromHexString(Tailwind.PURPLE_400))

        mystoriaComponent.append(Component
            .text("(${mystoriaPlugins.size} plugins)")
            .color(TextColor.fromHexString(Tailwind.GRAY_500))
        )
        mystoriaPlugins.forEach {
            mystoriaComponent.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(mystoriaComponent)

        val paperPlugins = (Bukkit.getPluginManager() as SimplePluginManager).paperPluginManager.plugins
        val paperComponent = Component
            .text("Paper: ")
            .color(TextColor.fromHexString(Tailwind.ORANGE_400))

        paperComponent.append(Component
            .text("(${paperPlugins.size} plugins)")
            .color(TextColor.fromHexString(Tailwind.GRAY_500))
        )
        paperPlugins.forEach {
            paperComponent.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(paperComponent)

        val plugins = Bukkit.getPluginManager().plugins.filterNot { it is ExtendedKotlinPlugin }
        val component = Component
            .text("Bukkit: ")
            .color(TextColor.fromHexString(Tailwind.BLUE_400))

        component.append(Component
            .text("(${plugins.size} plugins)")
            .color(TextColor.fromHexString(Tailwind.GRAY_500))
        )
        Bukkit.getPluginManager().plugins.filterNot { it is ExtendedKotlinPlugin }.forEach {
            component.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(component)
    }
}