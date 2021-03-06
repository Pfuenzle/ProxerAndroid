package me.proxer.app.util.http

import me.proxer.library.util.ProxerUrls
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Ruben Gees
 */
class ConnectionCloseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        return if (ProxerUrls.hasProxerProxyHost(oldRequest.url())) {
            val newRequest = oldRequest.newBuilder()
                .header("Connection", "close")
                .build()

            chain.proceed(newRequest)
        } else {
            chain.proceed(oldRequest)
        }
    }
}
