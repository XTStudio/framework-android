package com.xt.kimi.foundation

import android.net.Uri
import com.eclipsesource.v8.V8
import com.xt.kimi.KIMIPackage
import java.io.File

class URL(val uri: Uri) {

    val absoluteString = uri.toString()

}

fun KIMIPackage.installURL() {
    exporter.exportClass(URL::class.java, "URL")
    exporter.exportProperty(URL::class.java, "absoluteString", true)
    exporter.exportScript(URL::class.java, "Initializer.URLWithString = function(URLString, baseURL){ return new URL({URLString: URLString, baseURL: baseURL}) }")
    exporter.exportScript(URL::class.java, "Initializer.fileURLWithPath = function(filePath){ return new URL({filePath: filePath}) }")
    exporter.exportInitializer(URL::class.java) {
        (it.firstOrNull() as? Map<String, Any>)?.let {
            (it["URLString"] as? String)?.let {
                return@exportInitializer URL(Uri.parse(it))
            }
            (it["filePath"] as? String)?.let {
                return@exportInitializer URL(Uri.fromFile(File(it)))
            }
        }
        return@exportInitializer V8.getUndefined()
    }
}