package com.xt.kimi.uikit

import com.xt.endo.CGPoint
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

enum class UIGestureRecognizerState {
    possible,
    began,
    changed,
    ended,
    cancelled,
    failed,
}

open class UIGestureRecognizer {

    var state: UIGestureRecognizerState = UIGestureRecognizerState.possible
        internal set

    var enabled: Boolean = true

    var view: UIView? = null
        internal set

    fun requireGestureRecognizerToFail(otherGestureRecognizer: UIGestureRecognizer) {

    }

    fun locationInView(view: UIView?): CGPoint {
        return CGPoint(0.0, 0.0)
    }

    fun numberOfTouches(): Int {
        return 0
    }

    fun locationOfTouch(touchIndex: Int, view: UIView?): CGPoint {
        return CGPoint(0.0, 0.0)
    }

    internal open fun handleTouch(touches: Set<UITouch>) { }

}

fun KIMIPackage.installUIGestureRecognizer() {
    exporter.exportClass(UIGestureRecognizer::class.java, "UIGestureRecognizer")
    exporter.exportProperty(UIGestureRecognizer::class.java, "state", true)
    exporter.exportProperty(UIGestureRecognizer::class.java, "enabled")
    exporter.exportProperty(UIGestureRecognizer::class.java, "view", true)
    exporter.exportMethodToJavaScript(UIGestureRecognizer::class.java, "requireGestureRecognizerToFail")
    exporter.exportMethodToJavaScript(UIGestureRecognizer::class.java, "locationInView")
    exporter.exportMethodToJavaScript(UIGestureRecognizer::class.java, "numberOfTouches")
    exporter.exportMethodToJavaScript(UIGestureRecognizer::class.java, "locationOfTouch")
}