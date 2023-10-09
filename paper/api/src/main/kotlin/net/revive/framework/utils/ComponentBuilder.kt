package net.revive.framework.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor

inline fun buildComponent(component: Component, builder: ComponentBuilder.() -> Unit): Component = ComponentBuilder(component = component).apply(builder).build()

inline fun buildComponent(builder: ComponentBuilder.() -> Unit): Component = ComponentBuilder().apply(builder).build()

fun buildComponent(string: String, color: String) : Component = buildComponent {
    this.text(string, color)
    return build()
}

class ComponentBuilder(var component: Component = Component.empty()) {

    fun build(): Component = component

    fun append(child: Component) = apply {
        component = component.append(child)
    }

    fun text(string: String, lambda: (TextComponentBuilder) -> Unit) = apply {
        val builder = TextComponentBuilder(string)
        lambda.invoke(builder)
        append(builder.component)
    }

    fun text(string: String, color: String) = apply {
        append(Component.text(string).color(TextColor.fromHexString(color)))
    }
}

class TextComponentBuilder(string: String) {

    val component: TextComponent = Component.text(string)

    fun color(hex: String) = apply {
        component.color(TextColor.fromHexString(hex))
    }
}