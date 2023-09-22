package net.mystoria.framework.command.impl

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.mystoria.framework.annotation.command.AutoRegister
import net.mystoria.framework.command.FrameworkCommand
import net.mystoria.framework.constants.Tailwind
import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("plugins|pl|modules|module")
@AutoRegister
object PluginsCommand : FrameworkCommand() {

    @Default
    fun execute(sender: CommandSender) {
        val mystoriaComponent = Component
            .text("Mystoria Plugins: ")
            .color(TextColor.fromHexString(Tailwind.PURPLE_400))

        Bukkit.getPluginManager().plugins.filterIsInstance<ExtendedKotlinPlugin>().forEach {
            mystoriaComponent.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(mystoriaComponent)

        val paperComponent = Component
            .text("Paper Plugins: ")
            .color(TextColor.fromHexString(Tailwind.ORANGE_400))

        (Bukkit.getPluginManager() as SimplePluginManager).paperPluginManager.plugins.forEach {
            paperComponent.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(paperComponent)

        val component = Component
            .text("Bukkit Plugins: ")
            .color(TextColor.fromHexString(Tailwind.BLUE_400))

        Bukkit.getPluginManager().plugins.filterNot { it is ExtendedKotlinPlugin }.forEach {
            component.append(Component
                .text(it.name)
                .color(TextColor.fromHexString(if (it.isEnabled) Tailwind.EMERALD_400 else Tailwind.RED_600))
            )
        }
        sender.sendMessage(component)
    }
}