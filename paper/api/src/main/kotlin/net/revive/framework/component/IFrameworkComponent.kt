package net.revive.framework.component

import net.kyori.adventure.text.Component

interface IFrameworkComponent {
    fun build(): Component
}