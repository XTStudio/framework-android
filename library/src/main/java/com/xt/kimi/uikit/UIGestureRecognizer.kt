package com.xt.kimi.uikit

import com.xt.endo.CGPoint
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
        this.touches.firstOrNull()?.let { touch ->
            return touch.locationInView(view)
        }
        return CGPoint(0.0, 0.0)
    }

    fun numberOfTouches(): Int {
        return this.touches.count()
    }

    fun locationOfTouch(touchIndex: Int, view: UIView?): CGPoint {
        this.touches.toList()[touchIndex]?.let { touch ->
            return touch.locationInView(view)
        }
        return CGPoint(0.0, 0.0)
    }

    private var touches: Set<UITouch> = setOf()

    internal open fun handleTouch(touches: Set<UITouch>) { this.touches = touches }

    open fun handleEvent(name: String) { }

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