package com.xt.kimi.uikit

import android.os.Handler
import com.xt.endo.CGPoint
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import java.util.*
import kotlin.math.abs

class UILongPressGestureRecognizer: UIGestureRecognizer() {

    var numberOfTapsRequired = 1

    var numberOfTouchesRequired = 1

    var minimumPressDuration = 0.5

    var allowableMovement = 10

    private var timerTask: TimerTask? = null

    private var beganPoints: MutableMap<Int, CGPoint> = mutableMapOf()

    override fun handleTouch(touches: Set<UITouch>) {
        super.handleTouch(touches)
        touches.forEach {
            if (it.phase == UITouchPhase.began) {
                it.windowPoint?.let { windowPoint ->
                    this.beganPoints[it.identifier] = windowPoint
                }
                if (this.timerTask == null && this.beganPoints.count() >= this.numberOfTouchesRequired) {
                    val handler = Handler()
                    this.timerTask = kotlin.concurrent.timerTask {
                        handler.post {
                            if (UIView.recognizedGesture == null && this@UILongPressGestureRecognizer.state == UIGestureRecognizerState.possible) {
                                UIView.recognizedGesture = this@UILongPressGestureRecognizer
                                this@UILongPressGestureRecognizer.state = UIGestureRecognizerState.began
                                EDOJavaHelper.emit(this@UILongPressGestureRecognizer, "began", this@UILongPressGestureRecognizer)
                            }
                            else {
                                this@UILongPressGestureRecognizer.state = UIGestureRecognizerState.failed
                            }
                        }
                    }
                    sharedLongPressTimer.schedule(timerTask, (this.minimumPressDuration * 1000).toLong())
                }
            }
            else if (it.phase == UITouchPhase.moved) {
                if (this.state == UIGestureRecognizerState.possible) {
                    it.windowPoint?.let { windowPoint ->
                        this.beganPoints[it.identifier]?.let { beganPoint ->
                            if (abs(beganPoint.x - windowPoint.x) >= this.allowableMovement || abs(beganPoint.y - windowPoint.y) >= this.allowableMovement) {
                                this.state = UIGestureRecognizerState.failed
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
                this.timerTask?.cancel()
                this.timerTask = null
                if (it.identifier == 0) {
                    if (this.state == UIGestureRecognizerState.began || this.state == UIGestureRecognizerState.changed) {
                        this.state = UIGestureRecognizerState.ended
                        EDOJavaHelper.emit(this, "ended", this)
                        UIView.recognizedGesture = null
                    }
                    this.state = UIGestureRecognizerState.possible
                    this.beganPoints.clear()
                }
            }
            else if (it.phase == UITouchPhase.cancelled) {
                this.timerTask?.cancel()
                this.timerTask = null
                if (this.state == UIGestureRecognizerState.began || this.state == UIGestureRecognizerState.changed) {
                    this.state = UIGestureRecognizerState.cancelled
                    EDOJavaHelper.emit(this, "cancelled", this)
                    UIView.recognizedGesture = null
                }
                this.state = UIGestureRecognizerState.possible
                this.beganPoints.clear()
            }
        }
    }

    companion object {

        private val sharedLongPressTimer = Timer()

    }

}

fun KIMIPackage.installUILongPressGestureRecognizer() {
    exporter.exportClass(UILongPressGestureRecognizer::class.java, "UILongPressGestureRecognizer", "UIGestureRecognizer")
    exporter.exportProperty(UILongPressGestureRecognizer::class.java, "numberOfTapsRequired")
    exporter.exportProperty(UILongPressGestureRecognizer::class.java, "numberOfTouchesRequired")
    exporter.exportProperty(UILongPressGestureRecognizer::class.java, "minimumPressDuration")
    exporter.exportProperty(UILongPressGestureRecognizer::class.java, "allowableMovement")
}