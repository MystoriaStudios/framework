package net.revive.framework.sentry

import io.sentry.Sentry
import io.sentry.protocol.SentryId
import java.util.concurrent.Executors

class SentryService {

    val DATA_SOURCE = "https://bb1c52703dd8db80ecb927fb03209bbf@o1263915.ingest.sentry.io/4506094565785600"
    private val executor = Executors.newSingleThreadExecutor()
    var enabled = false

    fun log(throwable: Throwable, lambda: (SentryId?) -> Unit) {
        if (enabled) {
            executor.submit {
                Sentry.captureException(throwable).apply(lambda)
            }
        } else {
            lambda.invoke(null)
        }
    }

    fun configure() {
        net.revive.framework.Framework.use {
            it.log("Sentry", "Starting sentry initialization.")

            Sentry.init { options ->
                options.dsn = DATA_SOURCE

                options.connectionTimeoutMillis = 10000
                options.readTimeoutMillis = 10000
                options.environment = "production"
                options.tracesSampleRate = 1.0

                it.log("Sentry", "Successfully setup and listening on sentry.")
                enabled = true
            }
        }
    }
}