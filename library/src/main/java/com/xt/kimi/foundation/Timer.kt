package com.xt.kimi.foundation

import android.os.Handler
import android.os.Looper
import com.eclipsesource.v8.V8
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import java.util.*
import kotlin.concurrent.timerTask

class Timer(val timeInterval: Double, private val block: EDOCallback, val repeats: Boolean) {

    private val handler: Handler

    private lateinit var systemTimerTask: TimerTask

    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        this.handler = Handler()
        setupTimer()
    }

    private fun setupTimer() {
        this.systemTimerTask = timerTask {
            this@Timer.handler.post {
                this@Timer.block.invoke()
            }
            if (this@Timer.repeats) {
                this@Timer.setupTimer()
            }
        }
        sharedTimer.schedule(this.systemTimerTask, (timeInterval * 1000).toLong())
    }

    var valid: Boolean = true
        private set

    fun fire() {
        this@Timer.handler.post {
            this@Timer.block.invoke()
        }
    }

    fun invalidate() {
        this.systemTimerTask.cancel()
        this.valid = false
    }

    companion object {

        private val sharedTimer = java.util.Timer()

    }

}

fun KIMIPackage.installTimer() {
    exporter.exportClass(Timer::class.java, "Timer")
    exporter.exportInitializer(Timer::class.java) {
        val timeInterval = (it.getOrNull(0) as? Number)?.toDouble() ?: return@exportInitializer V8.getUndefined()
        val block = it.getOrNull(1) as? EDOCallback ?: return@exportInitializer V8.getUndefined()
        return@exportInitializer Timer(timeInterval, block, (it.getOrNull(2) as? Boolean) ?: false)
    }
    exporter.exportProperty(Timer::class.java, "valid", true)
    exporter.exportMethodToJavaScript(Timer::class.java, "fire")
    exporter.exportMethodToJavaScript(Timer::class.java, "invalidate")
    exporter.exportScript(Timer::class.java, "Initializer.sleep = function (timeInterval) { return new Promise((resolver) => { new Timer(timeInterval, () => { resolver(); }, false) })}", true)
}