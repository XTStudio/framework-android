package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

//todo: stub
open class UIRotationGestureRecognizer: UIGestureRecognizer() {

    var rotation = 1.0

    var velocity = 0.0
        private set

}

fun KIMIPackage.installUIRotationGestureRecognizer() {
    exporter.exportClass(UIRotationGestureRecognizer::class.java, "UIRotationGestureRecognizer", "UIGestureRecognizer")
    exporter.exportProperty(UIRotationGestureRecognizer::class.java, "rotation")
    exporter.exportProperty(UIRotationGestureRecognizer::class.java, "velocity", true)
}