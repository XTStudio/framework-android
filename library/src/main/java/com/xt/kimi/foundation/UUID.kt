package com.xt.kimi.foundation

import com.xt.kimi.KIMIPackage
import java.util.UUID

class UUID(UUIDString: String? = null) {

    val systemObject: UUID

    val UUIDString: String

    init {
        systemObject = if (UUIDString != null) UUID.fromString(UUIDString) else UUID.randomUUID()
        this.UUIDString = systemObject.toString()
    }

}

fun KIMIPackage.installUUID() {
    exporter.exportClass(com.xt.kimi.foundation.UUID::class.java, "UUID")
    exporter.exportProperty(com.xt.kimi.foundation.UUID::class.java, "UUIDString", true)
    exporter.exportInitializer(com.xt.kimi.foundation.UUID::class.java, {
        return@exportInitializer com.xt.kimi.foundation.UUID(it.firstOrNull() as? String)
    })
}