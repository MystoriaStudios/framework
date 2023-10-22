package net.revive.framework.utils

import net.minestom.server.MinecraftServer

object Tasks {

    val scheduler = MinecraftServer.getSchedulerManager()

    fun delayed(delay: Long, lambda: () -> Unit) {
        scheduler.scheduleNextTick(lambda)
    }
}