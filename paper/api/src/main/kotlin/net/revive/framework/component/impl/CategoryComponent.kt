package net.revive.framework.component.impl

import net.kyori.adventure.text.format.NamedTextColor
import net.revive.framework.component.IFrameworkComponent
import net.revive.framework.utils.applySmallCaps
import net.revive.framework.utils.buildComponent

class CategoryComponent(val string: String) : IFrameworkComponent {
    override fun build() = buildComponent(string.applySmallCaps(), NamedTextColor.DARK_GRAY.asHexString())
}