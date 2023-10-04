package net.revive.framework.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class SimpleEvent : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList

    /**
     * Calls the event and tests if cancelled.
     *
     * @return false if event was cancelled, if cancellable. otherwise true.
     */
    override fun callEvent(): Boolean {
        return super.callEvent()
    }
}