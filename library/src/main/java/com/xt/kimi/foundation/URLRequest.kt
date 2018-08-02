package com.xt.kimi.foundation

import com.eclipsesource.v8.V8
import com.xt.kimi.KIMIPackage

enum class URLRequestCachePolicy {
    useProtocol,
    ignoringLocalCache,
    returnCacheElseLoad,
    returnCacheDontLoad,
}

open class URLRequest(val URL: URL, val cachePolicy: URLRequestCachePolicy = URLRequestCachePolicy.useProtocol, val timeout: Double = 15.0) {

    open var HTTPMethod: String? = null
        internal set

    open var allHTTPHeaderFields: Map<String, Any>? = null
        internal set

    fun valueForHTTPHeaderField(field: String): Any? {
        return null
    }

    open var HTTPBody: Any? = null
        internal set

    fun mutable(): MutableURLRequest {
        val request = MutableURLRequest(this.URL, this.cachePolicy, this.timeout)
        request.HTTPMethod = this.HTTPMethod
        request.allHTTPHeaderFields = this.allHTTPHeaderFields
        request.HTTPBody = this.HTTPBody
        return request
    }

}

class MutableURLRequest(URL: URL, cachePolicy: URLRequestCachePolicy = URLRequestCachePolicy.useProtocol, timeout: Double = 15.0): URLRequest(URL, cachePolicy, timeout) {

    override var HTTPMethod: String? = null
        public set

    override var allHTTPHeaderFields: Map<String, Any>? = null
        public set

    override var HTTPBody: Any? = null
        public set

    fun setValueForHTTPHeaderField(value: String, field: String) {
        val mutableMap = this.allHTTPHeaderFields?.toMutableMap() ?: mutableMapOf()
        mutableMap[field] = value
        this.allHTTPHeaderFields = mutableMap.toMap()
    }

    fun immutable(): URLRequest {
        val request = URLRequest(this.URL, this.cachePolicy, this.timeout)
        request.HTTPMethod = this.HTTPMethod
        request.allHTTPHeaderFields = this.allHTTPHeaderFields
        request.HTTPBody = this.HTTPBody
        return request
    }

}

fun KIMIPackage.installURLRequest() {
    exporter.exportClass(URLRequest::class.java, "URLRequest")
    exporter.exportInitializer(URLRequest::class.java) {
        val url = if (0 < it.count()) it[0] as? URL ?: return@exportInitializer V8.getUndefined() else return@exportInitializer V8.getUndefined()
        val cachePolicy = if (1 < it.count()) it[1] as? URLRequestCachePolicy ?: URLRequestCachePolicy.useProtocol else URLRequestCachePolicy.useProtocol
        val timeout = if (2 < it.count()) (it[2] as? Number)?.toDouble() ?: 15.0 else 15.0
        return@exportInitializer URLRequest(url, cachePolicy, timeout)
    }
    exporter.exportProperty(URLRequest::class.java, "HTTPMethod", true)
    exporter.exportProperty(URLRequest::class.java, "URL", true)
    exporter.exportProperty(URLRequest::class.java, "allHTTPHeaderFields", true)
    exporter.exportMethodToJavaScript(URLRequest::class.java, "valueForHTTPHeaderField")
    exporter.exportProperty(URLRequest::class.java, "HTTPBody", true)
    exporter.exportMethodToJavaScript(URLRequest::class.java, "mutable")
    exporter.exportEnum("URLRequestCachePolicy", mapOf(
            Pair("useProtocol", URLRequestCachePolicy.useProtocol),
            Pair("ignoringLocalCache", URLRequestCachePolicy.ignoringLocalCache),
            Pair("returnCacheElseLoad", URLRequestCachePolicy.returnCacheElseLoad),
            Pair("returnCacheDontLoad", URLRequestCachePolicy.returnCacheDontLoad)
    ))
    exporter.exportClass(MutableURLRequest::class.java, "MutableURLRequest", "URLRequest")
    exporter.exportProperty(MutableURLRequest::class.java, "HTTPMethod")
    exporter.exportProperty(MutableURLRequest::class.java, "allHTTPHeaderFields")
    exporter.exportMethodToJavaScript(MutableURLRequest::class.java, "setValueForHTTPHeaderField")
    exporter.exportProperty(MutableURLRequest::class.java, "HTTPBody")
    exporter.exportInitializer(MutableURLRequest::class.java) {
        val url = if (0 < it.count()) it[0] as? URL ?: return@exportInitializer V8.getUndefined() else return@exportInitializer V8.getUndefined()
        val cachePolicy = if (1 < it.count()) it[1] as? URLRequestCachePolicy ?: URLRequestCachePolicy.useProtocol else URLRequestCachePolicy.useProtocol
        val timeout = if (2 < it.count()) (it[2] as? Number)?.toDouble() ?: 15.0 else 15.0
        return@exportInitializer MutableURLRequest(url, cachePolicy, timeout)
    }
}