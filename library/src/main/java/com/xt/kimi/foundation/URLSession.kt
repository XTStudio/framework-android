package com.xt.kimi.foundation

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.eclipsesource.v8.V8
import com.xt.endo.EDOCallback
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class URLSession {

    fun dataTask(request: Any, complete: EDOCallback): URLSessionTask? {
        var urlRequest: URLRequest? = null
        (request as? String)?.let { URLString ->
            try {
                urlRequest = URLRequest(URL(Uri.parse(URLString)))
            } catch (e: Exception) { }
        }
        (request as? URL)?.let { URL ->
            urlRequest = URLRequest(URL)
        }
        (request as? URLRequest)?.let {
            urlRequest = it
        }
        urlRequest?.let {
            try {
                return URLSessionTask(it, complete)
            } catch (e: Exception) {
                print(e)
            }
        }
        return null
    }

    companion object {

        @JvmField val shared = URLSession()

        internal val sharedCache = Cache(EDOExporter.sharedExporter.applicationContext?.cacheDir, 1024 * 1024 * 50)

        internal val sharedConnectionPool = ConnectionPool()

    }

}

class URLSessionTask(private val urlRequest: URLRequest, private val complete: EDOCallback) {

    private val handler: Handler
    private var okCall: Call? = null

    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        this.handler = Handler()
    }

    var state: URLSessionTaskState = URLSessionTaskState.suspended
        private set

    var countOfBytesExpectedToReceive: Int = 0
        private set

    var countOfBytesReceived: Int = 0
        private set

    var countOfBytesExpectedToSend: Int = 0
        private set

    var countOfBytesSent: Int = 0
        private set

    fun cancel() {
        this.state = URLSessionTaskState.cancelling
        this.okCall?.cancel()
    }

    fun resume() {
        this.buildCall()
        this.state = URLSessionTaskState.running
        this.okCall?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                if (this@URLSessionTask.state == URLSessionTaskState.cancelling) {
                    return
                }
                this@URLSessionTask.state = URLSessionTaskState.completed
                this@URLSessionTask.handler.post {
                    this@URLSessionTask.complete.invoke(V8.getUndefined(), V8.getUndefined(), Error(e?.message ?: "unknown error."))
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                this@URLSessionTask.state = URLSessionTaskState.completed
                val response = response ?: return
                val data: Data? = kotlin.run {
                    response.body()?.bytes()?.let {
                        return@run Data(it)
                    }
                    return@run null
                }
                this@URLSessionTask.handler.post {
                    this@URLSessionTask.complete.invoke(data ?: V8.getUndefined(), URLResponse(response))
                }
            }
        })
    }

    private fun buildCall() {
        val request = Request.Builder()
                .url(urlRequest.URL.absoluteString)
                .method(urlRequest.HTTPMethod ?: "GET",
                        kotlin.run {
                            if (urlRequest.HTTPMethod != "POST") {
                                return@run null
                            }
                            return@run URLSessionProgressRequestBody(RequestBody.create(
                                    MediaType.parse(urlRequest.valueForHTTPHeaderField("Content-Type") as? String ?: "application/octet-stream"),
                                    kotlin.run {
                                        (urlRequest.HTTPBody as? String)?.let {
                                            return@run it.toByteArray()
                                        }
                                        (urlRequest.HTTPBody as? Data)?.let {
                                            return@run it.byteArray
                                        }
                                        return@run ByteArray(0)
                                    }
                            ), object : URLSessionRequestProgressListener {
                                override fun update(bytesWritten: Long, contentLength: Long, done: Boolean) {
                                    this@URLSessionTask.countOfBytesSent = bytesWritten.toInt()
                                    this@URLSessionTask.countOfBytesExpectedToSend = contentLength.toInt()
                                }
                            })
                        }
                )
                .cacheControl(kotlin.run {
                    when (urlRequest.cachePolicy) {
                        URLRequestCachePolicy.useProtocol -> {
                            return@run CacheControl.Builder().build()
                        }
                        URLRequestCachePolicy.ignoringLocalCache -> {
                            return@run CacheControl.FORCE_NETWORK
                        }
                        URLRequestCachePolicy.returnCacheElseLoad -> {
                            return@run CacheControl.Builder().maxStale(Int.MAX_VALUE, TimeUnit.SECONDS).build()
                        }
                        URLRequestCachePolicy.returnCacheDontLoad -> {
                            return@run CacheControl.FORCE_CACHE
                        }
                        else -> {
                            return@run CacheControl.Builder().build()
                        }
                    }
                })
                .build()
        val client = OkHttpClient.Builder()
                .connectTimeout((urlRequest.timeout * 1000).toLong(), TimeUnit.MILLISECONDS)
                .cache(URLSession.sharedCache)
                .connectionPool(URLSession.sharedConnectionPool)
                .addNetworkInterceptor { chain ->
                    val originalResponse = chain.proceed(chain.request())
                    return@addNetworkInterceptor originalResponse.newBuilder()
                            .body(URLSessionProgressResponseBody(originalResponse.body()!!, object : URLSessionResponseProgressListener {
                                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                                    this@URLSessionTask.countOfBytesReceived = bytesRead.toInt()
                                    this@URLSessionTask.countOfBytesExpectedToReceive = contentLength.toInt()
                                }
                            }))
                            .build()
                }
                .build()
        this.okCall = client.newCall(request)
    }

}

enum class URLSessionTaskState {
    running,
    suspended,
    cancelling,
    completed,
}

fun KIMIPackage.installURLSession() {
    exporter.exportClass(URLSession::class.java, "URLSession")
    exporter.exportStaticProperty(URLSession::class.java, "shared")
    exporter.exportMethodToJavaScript(URLSession::class.java, "dataTask")
    exporter.exportClass(URLSessionTask::class.java, "URLSessionTask")
    exporter.exportProperty(URLSessionTask::class.java, "state")
    exporter.exportProperty(URLSessionTask::class.java, "countOfBytesExpectedToReceive")
    exporter.exportProperty(URLSessionTask::class.java, "countOfBytesReceived")
    exporter.exportProperty(URLSessionTask::class.java, "countOfBytesExpectedToSend")
    exporter.exportProperty(URLSessionTask::class.java, "countOfBytesSent")
    exporter.exportMethodToJavaScript(URLSessionTask::class.java, "cancel")
    exporter.exportMethodToJavaScript(URLSessionTask::class.java, "resume")
    exporter.exportEnum("URLSessionTaskState", mapOf(
            Pair("running", URLSessionTaskState.running),
            Pair("suspended", URLSessionTaskState.suspended),
            Pair("cancelling", URLSessionTaskState.cancelling),
            Pair("completed", URLSessionTaskState.completed)
    ))
}