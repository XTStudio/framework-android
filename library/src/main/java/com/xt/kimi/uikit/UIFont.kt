package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UIFont(val pointSize: Double, val fontStyle: String? = null, val fontName: String? = null)

fun KIMIPackage.installUIFont() {
    exporter.exportClass(UIFont::class.java, "UIFont")
    exporter.exportProperty(UIFont::class.java, "pointSize", true, true)
    exporter.exportProperty(UIFont::class.java, "fontStyle", true, true)
    exporter.exportProperty(UIFont::class.java, "fontName", true, true)
    exporter.exportInitializer(UIFont::class.java) {
        val pointSize = if (0 < it.count() && it[0] is Number) (it[0] as Number).toDouble() else 17.0
        val fontStyle = if (1 < it.count() && it[1] is String) it[1] as String else null
        val fontName = if (2 < it.count() && it[2] is String) it[2] as String else null
        return@exportInitializer UIFont(pointSize, fontStyle, fontName)
    }
}