package com.xt.kimi.uikit

import com.xt.endo.CGPoint

enum class UITouchPhase {
    began,
    moved,
    stationary,
    ended,
    cancelled,
}

class UITouch {

    var identifier: Int = 0

    var timestamp: Double = 0.0
        internal set

    var phase: UITouchPhase = UITouchPhase.cancelled
        internal set

    var tapCount: Int = 0
        internal set

    var window: UIWindow? = null
        internal set

    internal var windowPoint: CGPoint? = null

    var view: UIView? = null
        internal set

    var gestureRecognizers: List<Any> = listOf()
        internal set

    fun locationInView(view: UIView?): CGPoint {
        val aView = view ?: this.view ?: return CGPoint(0.0, 0.0)
        val windowPoint = this.windowPoint ?: return CGPoint(0.0, 0.0)
        return aView.convertPointFromWindow(windowPoint) ?: return CGPoint(0.0, 0.0)
    }

    fun previousLocationInView(view: UIView?): CGPoint {
        return CGPoint(0.0, 0.0)
    }

}