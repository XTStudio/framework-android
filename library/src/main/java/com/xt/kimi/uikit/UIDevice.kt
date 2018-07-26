package com.xt.kimi.uikit

import android.os.Build
import com.xt.kimi.KIMIPackage
import com.xt.kimi.foundation.UUID

class UIDevice {

    val name: String = Build.BRAND

    val model: String = Build.MODEL

    val systemName: String = "Android"

    val systemVersion: String = Build.VERSION.SDK_INT.toString()

    var identifierForVendor: UUID? = null
        internal set

    companion object {

        @JvmStatic val current = UIDevice()

    }

}

fun KIMIPackage.installUIDevice() {
    exporter.exportClass(UIDevice::class.java, "UIDevice")
    exporter.exportProperty(UIDevice::class.java, "name", true)
    exporter.exportProperty(UIDevice::class.java, "model", true)
    exporter.exportProperty(UIDevice::class.java, "systemName", true)
    exporter.exportProperty(UIDevice::class.java, "systemVersion", true)
    exporter.exportProperty(UIDevice::class.java, "identifierForVendor")
    exporter.exportStaticProperty(UIDevice::class.java, "current", true)
}