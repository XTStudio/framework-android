package com.xt.kimi.uikit

import com.xt.endo.CGPoint

enum class UITouchPhase {
    began,
    moved,
    stationary,
    ended,
    calcelled,
}

class UITouch {

    var identifier: Int = 0

    var timestamp: Double = 0.0
        internal set

    var phase: UITouchPhase = UITouchPhase.calcelled
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
        return CGPoint(0.0, 0.0)
    }

    fun previousLocationInView(view: UIView?): CGPoint {
        return CGPoint(0.0, 0.0)
    }

}