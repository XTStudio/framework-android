package com.xt.kimi.kimi

import com.xt.kimi.KIMIPackage

class KMCore {

    companion object {

        @JvmField val version = "0.1.0"

    }

}

fun KIMIPackage.installKMCore() {
    exporter.exportClass(KMCore::class.java, "KMCore")
    exporter.exportStaticProperty(KMCore::class.java, "version")
}