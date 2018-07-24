package com.xt.kimi.uikit

import com.xt.endo.CGPoint
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import kotlin.math.abs

class UIPanGestureRecognizer: UIGestureRecognizer() {

    fun translationInView(view: UIView?): CGPoint {
        val windowPoint = this.firstTouch?.windowPoint ?: return CGPoint(0.0, 0.0)
        val translationPoint = this.translationPoint ?: return CGPoint(0.0, 0.0)
        return CGPoint(windowPoint.x - translationPoint.x, windowPoint.y - translationPoint.y)
    }

    fun setTranslation(translation: CGPoint, inView: UIView?) {
        this.translationPoint = translation
    }

    fun velocityInView(view: UIView?): CGPoint {
        val x = UIView.sharedVelocityTracker.xVelocity.toDouble() / scale
        val y = UIView.sharedVelocityTracker.yVelocity.toDouble() / scale
        return CGPoint(x, y)
    }

    private var firstTouch: UITouch? = null
    private var translationPoint: CGPoint? = null
    private var beganPoints: MutableMap<Int, CGPoint> = mutableMapOf()

    override fun handleTouch(touches: Set<UITouch>) {
        super.handleTouch(touches)
        touches.forEach {
            if (it.identifier == 0) {
                firstTouch = it
            }
            if (it.phase == UITouchPhase.began) {
                if (UIView.recognizedGesture != null) { this.beganPoints.clear(); return }
                it.windowPoint?.let { windowPoint ->
                    this.beganPoints[it.identifier] = windowPoint
                }
                if (it.identifier == 0) {
                    this.translationPoint = it.windowPoint
                }
            }
            else if (it.phase == UITouchPhase.moved) {
                if (this.state == UIGestureRecognizerState.possible) {
                    if (UIView.recognizedGesture != null) {
                        this.state = UIGestureRecognizerState.failed
                        return
                    }
                    it.windowPoint?.let { windowPoint ->
                        this.beganPoints[it.identifier]?.let { beganPoint ->
                            if (abs(beganPoint.x - windowPoint.x) >= 8.0 || abs(beganPoint.y - windowPoint.y) >= 8.0) {
                                UIView.recognizedGesture = this
                                this.state = UIGestureRecognizerState.began
                                EDOJavaHelper.emit(this, "began", this)
                            }
                        }
                    }
                }
                else if (this.state == UIGestureRecognizerState.began || this.state == UIGestureRecognizerState.changed) {
                    this.state = UIGestureRecognizerState.changed
                    EDOJavaHelper.emit(this, "changed", this)
                }
            }
            else if (it.phase == UITouchPhase.ended) {
                if (this.state == UIGestureRecognizerState.began || this.state == UIGestureRecognizerState.changed) {
                    this.state = UIGestureRecognizerState.ended
                    EDOJavaHelper.emit(this, "ended", this)
                    UIView.recognizedGesture = null
                }
                if (it.identifier == 0) {
                    this.state = UIGestureRecognizerState.possible
                }
            }
            else if (it.phase == UITouchPhase.cancelled) {
                if (this.state == UIGestureRecognizerState.began || this.state == UIGestureRecognizerState.changed) {
                    this.state = UIGestureRecognizerState.cancelled
                    EDOJavaHelper.emit(this, "cancelled", this)
                    UIView.recognizedGesture = null
                }
                this.state = UIGestureRecognizerState.possible
            }
        }
    }

}

fun KIMIPackage.installUIPanGestureRecognizer() {
    exporter.exportClass(UIPanGestureRecognizer::class.java, "UIPanGestureRecognizer", "UIGestureRecognizer")
    exporter.exportMethodToJavaScript(UIPanGestureRecognizer::class.java, "translationInView")
    exporter.exportMethodToJavaScript(UIPanGestureRecognizer::class.java, "setTranslation")
    exporter.exportMethodToJavaScript(UIPanGestureRecognizer::class.java, "velocityInView")
}