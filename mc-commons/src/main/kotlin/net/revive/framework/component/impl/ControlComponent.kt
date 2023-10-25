package net.revive.framework.component.impl

import net.revive.framework.component.ClickType
import net.revive.framework.component.IFrameworkComponent
import net.revive.framework.constants.Tailwind
import net.revive.framework.utils.applySmallCaps
import net.revive.framework.utils.buildComponent

class ControlComponent(
    private val clickType: ClickType,
    private val action: String,
    val color: String = Tailwind.AMBER_400
) :
    IFrameworkComponent {
    override fun build() = buildComponent {
        text("${clickType.name.replace("_", " ")} click to $action".applySmallCaps(), color)
    }
}