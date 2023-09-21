package net.mystoria.framework.utils

import net.mystoria.framework.FrameworkPaperPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

object Tasks {
    
    val scheduler: BukkitScheduler get() = FrameworkPaperPlugin.instance.server.scheduler

    @JvmStatic
    fun sync(lambda: () -> Unit) {
        scheduler.runTask(FrameworkPaperPlugin.instance) { test ->
            lambda.invoke()
        }
    }

    @JvmStatic
    fun delayed(delay: Long, lambda: () -> Unit) {
        return scheduler.runTaskLater(FrameworkPaperPlugin.instance, { test ->
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: Runnable) : BukkitTask {
        return scheduler.runTaskLater(FrameworkPaperPlugin.instance, runnable, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: BukkitRunnable) : BukkitTask {
        return runnable.runTaskLater(FrameworkPaperPlugin.instance, delay)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, lambda: () -> Unit) {
        return scheduler.runTaskTimer(FrameworkPaperPlugin.instance, { test ->
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: Runnable) : BukkitTask {
        return scheduler.runTaskTimer(FrameworkPaperPlugin.instance, runnable, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: BukkitRunnable) : BukkitTask {
        return runnable.runTaskTimer(FrameworkPaperPlugin.instance, delay, interval)
    }

    @JvmStatic
    fun async(lambda: () -> Unit) {
        return scheduler.runTaskAsynchronously(FrameworkPaperPlugin.instance) { test ->
            lambda.invoke()
        }
    }

    @JvmStatic
    fun asyncDelayed(delay: Long, lambda: () -> Unit) {
        return scheduler.runTaskLaterAsynchronously(FrameworkPaperPlugin.instance, { test ->
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: Runnable, delay: Long) : BukkitTask {
        return scheduler.runTaskLaterAsynchronously(FrameworkPaperPlugin.instance, runnable, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: BukkitRunnable, delay: Long) : BukkitTask {
        return runnable.runTaskLaterAsynchronously(FrameworkPaperPlugin.instance, delay)
    }

    @JvmStatic
    fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit) {
        return scheduler.runTaskTimerAsynchronously(FrameworkPaperPlugin.instance, { test ->
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: Runnable, delay: Long, interval: Long) : BukkitTask {
        return scheduler.runTaskTimerAsynchronously(FrameworkPaperPlugin.instance, runnable, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: BukkitRunnable, delay: Long, interval: Long) : BukkitTask {
        return runnable.runTaskTimerAsynchronously(FrameworkPaperPlugin.instance, delay, interval)
    }
}