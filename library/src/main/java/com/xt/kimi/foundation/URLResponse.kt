package com.xt.kimi.foundation

import android.net.Uri
import com.xt.kimi.KIMIPackage
import okhttp3.Response

class URLResponse(response: Response) {

    val URL: URL? = try { URL(Uri.parse(response.request().url().toString())) } catch (e: Exception) { null }

    val expectedContentLength: Int = try { response.header("Content-Length")?.toInt() ?: 0 } catch (e: Exception) { 0 }

    val MIMEType: String? = kotlin.run {
        response.header("Content-Type")?.takeIf { it.contains(";") }?.let {
            return@run it.split(";").firstOrNull()?.trim()
        }
        return@run null
    }

    val textEncodingName: String? = kotlin.run {
        response.header("Content-Type")?.takeIf { it.contains("charset=") }?.let {
            return@run it.split("charset=").lastOrNull()?.trim()
        }
        return@run null
    }

    val statusCode: Int = response.code()

    val allHeaderFields: Map<String, Any> = kotlin.run {
        var headerFields: MutableMap<String, Any> = mutableMapOf()
        response.headers().names().forEach {
            headerFields[it] = response.header(it) ?: ""
        }
        return@run headerFields.toMap()
    }

}

fun KIMIPackage.installURLResponse() {
    exporter.exportClass(URLResponse::class.java, "URLResponse")
    exporter.exportProperty(URLResponse::class.java, "URL", true, true)
    exporter.exportProperty(URLResponse::class.java, "expectedContentLength", true, true)
    exporter.exportProperty(URLResponse::class.java, "MIMEType", true, true)
    exporter.exportProperty(URLResponse::class.java, "textEncodingName", true, true)
    exporter.exportProperty(URLResponse::class.java, "statusCode", true, true)
    exporter.exportProperty(URLResponse::class.java, "allHeaderFields", true, true)
}