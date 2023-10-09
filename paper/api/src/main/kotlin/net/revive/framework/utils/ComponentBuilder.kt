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

    fun append(child: Component) : ComponentBuilder {
        component.append(child)
        return this
    }

    fun text(string: String, lambda: (TextComponentBuilder) -> Unit) : ComponentBuilder {
        val builder = TextComponentBuilder(string, this)
        lambda.invoke(builder)
        append(builder.component)
        return this
    }

    fun text(string: String, color: String) : ComponentBuilder {
        append(Component.text(string).color(TextColor.fromHexString(color)))
        return this
    }
}

class TextComponentBuilder(string: String, val builder: ComponentBuilder) {

    val component: TextComponent = Component.text(string)

    fun color(hex: String) : TextComponentBuilder {
        component.color(TextColor.fromHexString(hex))
        return this
    }
}