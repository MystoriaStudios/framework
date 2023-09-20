package net.mystoria.framework

import kotlin.properties.Delegates

/**
 * Contains code that is not platform
 * dependent to be used for data retrieving
 * this is either for the [PAPER, VELOCITY or INDEPENDENT]
 * platforms.
 */
abstract class Framework {

    companion object {
        private var instance by Delegates.notNull<Framework>()

        fun use(lambda: (Framework) -> Unit) = lambda.invoke(instance)
    }

    abstract fun constructNewRedisConnection()
    abstract fun constructNewMongoConnection()

    abstract fun debug(from: String, message: String)
}

fun String.debug(from: String) = Framework.use {
    it.debug(from, this)
}