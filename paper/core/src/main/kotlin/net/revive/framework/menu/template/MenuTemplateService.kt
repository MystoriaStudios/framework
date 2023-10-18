package net.revive.framework.menu.template

import net.revive.framework.Framework
import net.revive.framework.PaperFrameworkPlugin
import net.revive.framework.constants.Tailwind
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.utils.buildComponent
import net.revive.framework.utils.itemBuilder
import org.bukkit.Material
import java.io.File

@Service
object MenuTemplateService {

    val templates = mutableMapOf<String, MenuTemplate>()

    @Configure
    fun configure() {
        reload()
    }

    fun reload() {
        templates.clear()
        val directory = File(PaperFrameworkPlugin.instance.getBaseFolder(), "menu-templates")
        if (!directory.exists()) {
            directory.mkdirs()

            val example = File(directory, "example.json")
            if (example.createNewFile()) example.writeText(Framework.useWithReturn {
                it.serializer.serialize(MenuTemplate(
                    "example",
                    buttons = mutableListOf(
                        MenuTemplate.Button(
                            1,
                            itemBuilder {
                                this.type(Material.OAK_SIGN)
                                this.lore(
                                    listOf(
                                        buildComponent("test", Tailwind.AMBER_400)
                                    )
                                )
                            }
                        )
                    )
                ))
            })
        }

        directory.listFiles()?.forEach { file ->
            templates[file.name] = Framework.useWithReturn {
                it.log("Framework", "[Menu-Template] Loaded from file ${file.path}.")
                it.serializer.deserialize(MenuTemplate::class, file.readText())
            }
        }
    }
}