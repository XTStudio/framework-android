package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

//todo: stub
class UIPinchGestureRecognizer: UIGestureRecognizer() {

    var scale = 1.0

    var velocity = 0.0
        private set

    override fun handleTouch(touches: Set<UITouch>) {
        super.handleTouch(touches)
    }

}

fun KIMIPackage.installUIPinchGestureRecognizer() {
    exporter.exportClass(UIPinchGestureRecognizer::class.java, "UIPinchGestureRecognizer", "UIGestureRecognizer")
    exporter.exportProperty(UIPinchGestureRecognizer::class.java, "scale")
    exporter.exportProperty(UIPinchGestureRecognizer::class.java, "velocity", true)
}