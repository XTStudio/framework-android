package com.xt.kimi.kimi

import com.xt.kimi.KIMIPackage

class KMCore {

    companion object {

        @JvmField val version = "0.7.0"

        @JvmField var hostVersion: String = ""

    }

}

fun KIMIPackage.installKMCore() {
    exporter.exportClass(KMCore::class.java, "KMCore")
    exporter.exportStaticProperty(KMCore::class.java, "version", true, true)
    exporter.exportStaticProperty(KMCore::class.java, "hostVersion", true, true)
}