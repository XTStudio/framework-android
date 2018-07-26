package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.kimi.KIMIPackage

class UIScreen {

    var bounds: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        internal set

    var scale: Double = 1.0
        internal set

    companion object {

        @JvmStatic val main = UIScreen()

    }

}

fun KIMIPackage.installUIScreen() {
    exporter.exportClass(UIScreen::class.java, "UIScreen")
    exporter.exportProperty(UIScreen::class.java, "bounds", true)
    exporter.exportProperty(UIScreen::class.java, "scale", true)
    exporter.exportStaticProperty(UIScreen::class.java, "main", true)
}