package net.revive.framework.menu.template

import net.revive.framework.Framework
import net.revive.framework.PaperFramework
import net.revive.framework.PaperFrameworkPlugin
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import java.awt.print.Paper
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
                    "example"
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