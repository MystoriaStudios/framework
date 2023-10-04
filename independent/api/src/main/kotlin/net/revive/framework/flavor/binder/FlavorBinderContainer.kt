package net.revive.framework.flavor.binder

import net.revive.framework.flavor.FlavorBinder

/**
 * @author Dash
 * @since 9/6/2022
 */
abstract class FlavorBinderContainer
{
    internal val binders = mutableListOf<FlavorBinder<*>>()

    abstract fun populate()

    fun bind(`object`: Any) = FlavorBinderMultiType(this, `object`)
}
