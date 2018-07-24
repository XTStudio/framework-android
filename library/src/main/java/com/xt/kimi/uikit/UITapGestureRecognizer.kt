package com.xt.kimi.uikit

import android.os.Handler
import com.xt.endo.CGPoint
import com.xt.endo.EDOCallback
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import kotlin.math.abs

class UITapGestureRecognizer : UIGestureRecognizer() {

    var numberOfTapsRequired = 1

    var numberOfTouchesRequired = 1

    private var beganPoints: MutableMap<Int, CGPoint> = mutableMapOf()

    private var validPointsCount = 0

    override fun handleTouch(touches: Set<UITouch>) {
        super.handleTouch(touches)
        touches.forEach {
            if (it.phase == UITouchPhase.began) {
                it.windowPoint?.let { windowPoint ->
                    this.beganPoints[it.identifier] = windowPoint
                }
            }
            else if (it.phase == UITouchPhase.moved) {
                it.windowPoint?.let { windowPoint ->
                    this.beganPoints[it.identifier]?.let { beganPoint ->
                        if (abs(beganPoint.x - windowPoint.x) >= 22.0 || abs(beganPoint.y - windowPoint.y) >= 22.0) {
                            this.beganPoints.remove(it.identifier)
                        }
                    }
                }
            }
            else if (it.phase == UITouchPhase.ended) {
                if (UIView.recognizedGesture != null) {
                    this.beganPoints.clear()
                    this.state = UIGestureRecognizerState.possible
                    this.validPointsCount = 0
                    return
                }
                if (it.tapCount >= this.numberOfTapsRequired && this.beganPoints[it.identifier] != null) {
                    this.validPointsCount++
                }
                this.beganPoints.remove(it.identifier)
                if (this.validPointsCount >= this.numberOfTouchesRequired) {
                    val handler = Handler()
                    UIView.recognizedGesture = this
                    this.state = UIGestureRecognizerState.ended
                    EDOJavaHelper.emit(this, "touch", this)
                    handler.post {
                        UIView.recognizedGesture = null
                    }
                }
                if (this.beganPoints.keys.count() == 0 || this.state == UIGestureRecognizerState.ended) {
                    this.state = UIGestureRecognizerState.possible
                    this.validPointsCount = 0
                }
            }
        }

    }

}

fun KIMIPackage.installUITapGestureRecognizer() {
    exporter.exportClass(UITapGestureRecognizer::class.java, "UITapGestureRecognizer", "UIGestureRecognizer")
    exporter.exportProperty(UITapGestureRecognizer::class.java, "numberOfTapsRequired")
    exporter.exportProperty(UITapGestureRecognizer::class.java, "numberOfTouchesRequired")
}