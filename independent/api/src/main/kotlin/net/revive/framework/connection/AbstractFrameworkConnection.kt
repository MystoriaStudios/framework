package net.revive.framework.connection

import java.util.logging.Logger

abstract class AbstractFrameworkConnection<C, R> {
    companion object {
        private val logger: Logger = Logger.getLogger(AbstractFrameworkConnection::class.simpleName)

        fun useLogger(lambda: (Logger) -> Unit) {
            lambda.invoke(logger)
        }
    }

    abstract fun useResource(lambda: R.() -> Unit)
    abstract fun <T> useResourceWithReturn(lambda: R.() -> T): T?

    abstract fun getConnection(): C
    abstract fun setConnection(connection: C)

    abstract fun createNewConnection(): C

    abstract fun close()
}