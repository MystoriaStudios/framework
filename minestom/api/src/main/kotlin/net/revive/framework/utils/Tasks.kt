package net.revive.framework.utils

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.Task
import java.time.Duration

object Tasks {

    fun sync(lambda: () -> Unit): Task {
        return MinecraftServer.getSchedulerManager().buildTask {
            lambda.invoke()
        }.executionType(ExecutionType.SYNC).schedule()
    }

    fun delayed(delay: Duration, lambda: () -> Unit): Task {
        return MinecraftServer.getSchedulerManager().buildTask {
            lambda.invoke()
        }.executionType(ExecutionType.SYNC).delay(delay).schedule()
    }

    fun timer(delay: Duration, lambda: () -> Unit): Task {
        return MinecraftServer.getSchedulerManager().buildTask {
            lambda.invoke()
        }.executionType(ExecutionType.SYNC).repeat(delay).schedule()
    }

    fun asyncDelayed(delay: Duration, lambda: () -> Unit): Task {
        return MinecraftServer.getSchedulerManager().buildTask {
            lambda.invoke()
        }.executionType(ExecutionType.ASYNC).delay(delay).schedule()
    }

    fun asyncTimer(delay: Duration, lambda: () -> Unit): Task {
        return MinecraftServer.getSchedulerManager().buildTask {
            lambda.invoke()
        }.executionType(ExecutionType.ASYNC).repeat(delay).schedule()
    }
}