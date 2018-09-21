package com.xt.kimi.coregraphics

import android.animation.ValueAnimator
import android.os.SystemClock
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

class CADisplayLink(val vsyncBlock: EDOCallback?) {

    var timestamp: Double = 0.0
        private set

    private var animator: ValueAnimator? = null

    fun active() {
        this.animator?.cancel()
        this.animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        this.animator?.duration = 10000
        this.animator?.repeatCount = 99999
        this.animator?.addUpdateListener {
            this.timestamp = SystemClock.uptimeMillis().toDouble() / 1000.0
            this.vsyncBlock?.invoke()
        }
        this.animator?.start()
    }

    fun invalidate() {
        this.animator?.cancel()
        this.animator = null
    }

}

fun KIMIPackage.installCADisplayLink() {
    exporter.exportClass(CADisplayLink::class.java, "CADisplayLink")
    exporter.exportMethodToJavaScript(CADisplayLink::class.java, "active")
    exporter.exportMethodToJavaScript(CADisplayLink::class.java, "invalidate")
    exporter.exportProperty(CADisplayLink::class.java, "timestamp", true)
    exporter.exportInitializer(CADisplayLink::class.java) {
        return@exportInitializer CADisplayLink(it.firstOrNull() as? EDOCallback)
    }
}