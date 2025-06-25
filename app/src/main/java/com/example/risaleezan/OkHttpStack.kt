package com.example.risaleezan

import com.android.volley.toolbox.HurlStack
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class OkHttpStack : HurlStack() {
    private val okHttpClient = OkHttpClient()

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        val okUrl = url.toString().toHttpUrl()
        val request = Request.Builder().url(okUrl).build()
        val response = okHttpClient.newCall(request).execute()
        return OkHttpUrlHttpUrlConnection(url, response)
    }
}