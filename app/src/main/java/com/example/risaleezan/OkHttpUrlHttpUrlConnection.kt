package com.example.risaleezan

import okhttp3.Response
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class OkHttpUrlHttpUrlConnection(url: URL, private val response: Response) : HttpURLConnection(url) {

    override fun connect() {}
    override fun disconnect() {
        response.body?.close()
    }
    override fun usingProxy(): Boolean = false

    override fun getInputStream(): InputStream {
        return response.body?.byteStream() ?: ByteArrayInputStream(ByteArray(0))
    }

    override fun getErrorStream(): InputStream? {
        return if (response.isSuccessful) null else response.body?.byteStream()
    }

    override fun getHeaderField(name: String?): String? {
        return name?.let { response.header(it) }
    }

    override fun getResponseCode(): Int {
        return response.code
    }

    override fun getResponseMessage(): String {
        return response.message.ifEmpty { "" }
    }

    override fun getOutputStream(): OutputStream {
        throw UnsupportedOperationException()
    }
}