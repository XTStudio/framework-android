package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UIScreen {

    var bounds: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        internal set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "bounds")
        }

    var scale: Double = 1.0
        internal set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "scale")
        }

    companion object {

        @JvmStatic val main = UIScreen()

    }

}

fun KIMIPackage.installUIScreen() {
    exporter.exportClass(UIScreen::class.java, "UIScreen")
    exporter.exportProperty(UIScreen::class.java, "bounds", true, true)
    exporter.exportProperty(UIScreen::class.java, "scale", true, true)
    exporter.exportStaticProperty(UIScreen::class.java, "main", true, true)
}