package net.revive.framework.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.revive.framework.component.IFrameworkComponent

inline fun buildComponent(component: Component, builder: ComponentBuilder.() -> Unit): Component =
    ComponentBuilder(component = component).apply(builder).build()

inline fun buildComponent(builder: ComponentBuilder.() -> Unit): Component = ComponentBuilder().apply(builder).build()

fun buildComponent(string: String, color: String): Component = buildComponent {
    this.text(string, color)
    return build()
}

class ComponentBuilder(var component: Component = Component.empty()) {

    fun build(): Component = component.decoration(TextDecoration.ITALIC, false)

    fun append(child: Component) = apply {
        component = component.append(child)
    }

    // use this to pre build very small lightweight components that will be repeatable
    fun append(child: IFrameworkComponent) = apply {
        append(child.build())
    }

    fun text(string: String, lambda: (TextComponentBuilder) -> Unit) = apply {
        val builder = TextComponentBuilder(string)
        lambda.invoke(builder)
        append(builder.component)
    }

    fun text(string: String, color: String) = apply {
        append(Component.text(string).decoration(TextDecoration.ITALIC, false).color(TextColor.fromHexString(color)))
    }
}

class TextComponentBuilder(string: String) {

    var component: TextComponent = Component.text(string).decoration(TextDecoration.ITALIC, false)

    fun color(hex: String) = apply {
        component = component.color(TextColor.fromHexString(hex))
    }

    fun decorate(decoration: TextDecoration) = apply {
        component = component.decorate(decoration)
    }
}