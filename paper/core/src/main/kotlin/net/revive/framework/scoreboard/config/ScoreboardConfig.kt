package net.revive.framework.scoreboard.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.config.JsonConfig
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.buildComponent

@JsonConfig("scoreboard.json")
class ScoreboardConfig(
    val template: ScoreboardTemplate = ScoreboardTemplate(),
    val enabled: Boolean = true
) {
    class ScoreboardTemplate(
        var title: Component = buildComponent {
            text("RANDOMCRAFT") {
                it.color(Tailwind.GREEN_600)
                it.decorate(TextDecoration.BOLD)
            }
        },
        var lines: MutableList<Component> = mutableListOf(
            buildComponent {
                text("test", Tailwind.GREEN_300)
            },
            Component.empty(),
            buildComponent {
                text("test", Tailwind.GREEN_600)
            }
        )
    )
}