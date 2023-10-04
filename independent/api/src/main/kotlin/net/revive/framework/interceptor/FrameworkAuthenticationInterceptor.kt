package net.revive.framework.interceptor

import net.revive.framework.constants.Deployment
import okhttp3.Interceptor
import okhttp3.Response

object FrameworkAuthenticationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain
            .request()
            .newBuilder()
            .header("Content-Type", "application/json")
            .header("Framework-API-Key", Deployment.Security.API_KEY)

        return chain.proceed(requestBuilder.build())
    }
}