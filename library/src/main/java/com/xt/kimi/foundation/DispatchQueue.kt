package com.xt.kimi.foundation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

class DispatchQueue(val identifier: String) {

    private val handler: Handler

    init {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        this.handler = Handler()
    }

    fun async(asyncBlock: EDOCallback) {
        this.handler.post {
            asyncBlock.invoke()
        }
    }

    fun asyncAfter(delayInSeconds: Double, asyncBlock: EDOCallback) {
        this.handler.postAtTime({
            asyncBlock.invoke()
        }, SystemClock.uptimeMillis() + (delayInSeconds * 1000).toLong())
    }

    companion object {

        @JvmStatic val main = DispatchQueue("main")

    }

}

fun KIMIPackage.installDispatchQueue() {
    exporter.exportClass(DispatchQueue::class.java, "DispatchQueue")
    exporter.exportMethodToJavaScript(DispatchQueue::class.java, "async")
    exporter.exportMethodToJavaScript(DispatchQueue::class.java, "asyncAfter")
    exporter.exportInitializer(DispatchQueue::class.java, {
        val identifier = it.firstOrNull() as? String ?: return@exportInitializer DispatchQueue.main
        if (identifier == "main") {
            return@exportInitializer DispatchQueue.main
        }
        return@exportInitializer DispatchQueue.main
    })
    exporter.exportStaticProperty(DispatchQueue::class.java, "main", true)
}