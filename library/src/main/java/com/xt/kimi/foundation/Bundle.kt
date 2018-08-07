package com.xt.kimi.foundation

import android.net.Uri
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import java.io.File

enum class BundleType {
    native,
    js,
}

class Bundle(val bundleType: BundleType) {

    fun resourcePath(name: String, type: String?, inDirectory: String?): String? {
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
                return "/android_assets${inDirectory ?: ""}/$it"
            }
        }
        return null
    }

    fun resourceURL(name: String, type: String, inDirectory: String?): URL? {
        this.resourcePath(name, type, inDirectory)?.let {
            return URL(Uri.fromFile(File(it)))
        }
        return null
    }

    companion object {

        val native = Bundle(BundleType.native)
        val js = Bundle(BundleType.js)

    }

}

fun KIMIPackage.installBundle() {
    exporter.exportClass(Bundle::class.java, "Bundle")
    exporter.exportStaticProperty(Bundle::class.java, "native", true)
    exporter.exportStaticProperty(Bundle::class.java, "js", true)
    exporter.exportMethodToJavaScript(Bundle::class.java, "resourcePath")
    exporter.exportMethodToJavaScript(Bundle::class.java, "resourceURL")
}