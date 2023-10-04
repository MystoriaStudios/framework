package net.revive.framework.utils

fun Class<*>.objectInstance(): Any?
{
    return kotlin.runCatching {
        getDeclaredField("INSTANCE").get(null) ?: kotlin.objectInstance
    }.getOrNull().also { any ->
        if (any == null) constructors.forEach {
            it.isAccessible = true
        }
    }
}