package com.xt.kimi.foundation

import android.net.Uri
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import java.io.File

enum class BundleType {
    native,
    js,
}

class JSBundle: Bundle(BundleType.js) {

    internal val resources: MutableMap<String, String> = mutableMapOf()

    override fun resourcePath(name: String, type: String?, inDirectory: String?): String? {
        val files = inDirectory?.let { inDirectory ->
            return@let this.resources.keys.filter { it.startsWith("$inDirectory/") }
        } ?: kotlin.run {
            return@run this.resources.keys
        }
        files.firstOrNull { fileName ->
            if (fileName.startsWith(name)){
                type?.let {
                    if (fileName.endsWith(".$it")) {
                        return@firstOrNull true
                    }
                }
            }
            return@firstOrNull false
        }?.let {
            inDirectory?.let { inDirectory ->
                return "/com.xt.bundle.js/$inDirectory/$it"
            } ?: kotlin.run {
                return "/com.xt.bundle.js/$it"
            }
        }
        return null
    }

    override fun addResource(path: String, base64String: String) {
        this.resources[path] = base64String
    }

}

open class Bundle(val bundleType: BundleType) {

    open fun resourcePath(name: String, type: String?, inDirectory: String?): String? {
        if (this.bundleType == BundleType.native) {
            val applicationContext = EDOExporter.sharedExporter.applicationContext ?: return null
            applicationContext.assets.list(inDirectory ?: "").firstOrNull { fileName ->
                if (fileName.startsWith(name)){
                    type?.let {
                        if (fileName.endsWith(".$it")) {
                            return@firstOrNull true
                        }
                    }
                }
                return@firstOrNull false
            }?.let {
                inDirectory?.let { inDirectory ->
                    return "/android_assets/$inDirectory/$it"
                } ?: kotlin.run {
                    return "/android_assets/$it"
                }
            }
        }
        return null
    }

    open fun resourceURL(name: String, type: String, inDirectory: String?): URL? {
        this.resourcePath(name, type, inDirectory)?.let {
            return URL(Uri.fromFile(File(it)))
        }
        return null
    }

    open fun addResource(path: String, base64String: String) {

    }

    companion object {

        val native = Bundle(BundleType.native)
        val js = JSBundle()

    }

}

fun KIMIPackage.installBundle() {
    exporter.exportClass(Bundle::class.java, "Bundle")
    exporter.exportStaticProperty(Bundle::class.java, "native", true, true)
    exporter.exportStaticProperty(Bundle::class.java, "js", true, true)
    exporter.exportMethodToJavaScript(Bundle::class.java, "resourcePath")
    exporter.exportMethodToJavaScript(Bundle::class.java, "resourceURL")
    exporter.exportMethodToJavaScript(Bundle::class.java, "addResource")
}