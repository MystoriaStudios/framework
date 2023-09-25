package net.mystoria.framework.utils

import net.mystoria.framework.plugin.ExtendedKotlinPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

object Tasks {
    
    lateinit var plugin: ExtendedKotlinPlugin
    val scheduler: BukkitScheduler get() = plugin.server.scheduler

    @JvmStatic
    fun sync(lambda: () -> Unit) {
        scheduler.runTask(plugin) { test ->
            lambda.invoke()
        }
    }

    @JvmStatic
    fun delayed(delay: Long, lambda: () -> Unit) {
        return scheduler.runTaskLater(plugin, { test ->
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: Runnable) : BukkitTask {
        return scheduler.runTaskLater(plugin, runnable, delay)
    }

    @JvmStatic
    fun delayed(delay: Long, runnable: BukkitRunnable) : BukkitTask {
        return runnable.runTaskLater(plugin, delay)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, lambda: () -> Unit) {
        return scheduler.runTaskTimer(plugin, { test ->
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: Runnable) : BukkitTask {
        return scheduler.runTaskTimer(plugin, runnable, delay, interval)
    }

    @JvmStatic
    fun timer(delay: Long, interval: Long, runnable: BukkitRunnable) : BukkitTask {
        return runnable.runTaskTimer(plugin, delay, interval)
    }

    @JvmStatic
    fun async(lambda: () -> Unit) {
        return scheduler.runTaskAsynchronously(plugin) { test ->
            lambda.invoke()
        }
    }

    @JvmStatic
    fun asyncDelayed(delay: Long, lambda: () -> Unit) {
        return scheduler.runTaskLaterAsynchronously(plugin, { test ->
            lambda.invoke()
        }, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: Runnable, delay: Long) : BukkitTask {
        return scheduler.runTaskLaterAsynchronously(plugin, runnable, delay)
    }

    @JvmStatic
    fun asyncDelayed(runnable: BukkitRunnable, delay: Long) : BukkitTask {
        return runnable.runTaskLaterAsynchronously(plugin, delay)
    }

    @JvmStatic
    fun asyncTimer(delay: Long, interval: Long, lambda: () -> Unit) {
        return scheduler.runTaskTimerAsynchronously(plugin, { test ->
            lambda.invoke()
        }, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: Runnable, delay: Long, interval: Long) : BukkitTask {
        return scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, interval)
    }

    @JvmStatic
    fun asyncTimer(runnable: BukkitRunnable, delay: Long, interval: Long) : BukkitTask {
        return runnable.runTaskTimerAsynchronously(plugin, delay, interval)
    }
}