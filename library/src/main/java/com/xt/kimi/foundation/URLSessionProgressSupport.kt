package com.xt.kimi.foundation

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.*
import java.io.IOException
import okio.Okio
import okio.BufferedSink
import okio.Sink
import java.io.FilterOutputStream
import java.io.OutputStream


internal class URLSessionProgressResponseBody internal constructor(private val responseBody: ResponseBody, private val progressListener: URLSessionResponseProgressListener) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            internal var totalBytesRead = 0L
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != (-1).toLong()) bytesRead else 0
                progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == (-1).toLong())
                return bytesRead
            }
        }
    }

}

internal class URLSessionProgressRequestBody internal constructor(private val requestBody: RequestBody, private val progressListener: URLSessionRequestProgressListener): RequestBody() {

    private var bufferedSink: BufferedSink? = null
    private var contentLength = requestBody.contentLength()

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        return contentLength
    }

    override fun writeTo(sink: BufferedSink?) {
        val sink = sink ?: return
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(this.outputStreamSink(sink))
        }
        val bufferedSink = bufferedSink ?: return
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    private fun outputStreamSink(sink: BufferedSink): Sink {
        return Okio.sink(object : URLSessionCountingOutputStream(sink.outputStream()) {
            @Throws(IOException::class)
            override fun write(data: ByteArray, offset: Int, byteCount: Int) {
                super.write(data, offset, byteCount)
                sendProgressUpdate()
            }

            @Throws(IOException::class)
            override fun write(data: Int) {
                super.write(data)
                sendProgressUpdate()
            }

            @Throws(IOException::class)
            private fun sendProgressUpdate() {
                val bytesWritten = this.count
                val contentLength = contentLength()
                progressListener.update(bytesWritten, contentLength, bytesWritten == contentLength)
            }
        })
    }

}

internal interface URLSessionResponseProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

internal interface URLSessionRequestProgressListener {
    fun update(bytesWritten: Long, contentLength: Long, done: Boolean)
}

internal open class URLSessionCountingOutputStream(out: OutputStream) : FilterOutputStream(out) {

    var count: Long = 0
        private set

    init {
        count = 0
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        out.write(b, off, len)
        count += len.toLong()
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        out.write(b)
        count++
    }

    @Throws(IOException::class)
    override fun close() {
        out.close()
    }

}