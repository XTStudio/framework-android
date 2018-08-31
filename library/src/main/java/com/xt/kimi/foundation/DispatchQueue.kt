package com.xt.kimi.foundation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.xt.endo.EDOCallback
import com.xt.endo.EDOExporter
import com.xt.endo.EDOObjectTransfer
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage

class DispatchQueue(val identifier: String) {

    private val handler: Handler
    private var operationContext: JSContext? = null

    init {
        assert(identifier == "main" && Looper.myLooper() == Looper.getMainLooper())
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        this.handler = Handler()
    }

    fun async(asyncBlock: EDOCallback) {
        if (this.handler.looper != Looper.myLooper()) {
            return
        }
        this.handler.post {
            asyncBlock.invoke()
        }
    }

    fun asyncAfter(delayInSeconds: Double, asyncBlock: EDOCallback) {
        if (this.handler.looper != Looper.myLooper()) {
            return
        }
        this.handler.postAtTime({
            asyncBlock.invoke()
        }, SystemClock.uptimeMillis() + (delayInSeconds * 1000).toLong())
    }

    fun _isolate(script: String, arguments: List<Any>) {
        this.handler.post {
            if (this.operationContext == null) {
                val context = JSContext()
                EDOExporter.sharedExporter.exportWithContext(context)
                this.operationContext = context
            }
            this.operationContext?.let { context ->
                context.evaluateScript("var __isolate_exec_func = $script")
                context["__isolate_exec_func"]?.callWithArguments(arguments.map {
                    return@map EDOObjectTransfer.convertToJSValueWithJavaValue(it, context.runtime)
                })
            }
        }
    }

    companion object {

        @JvmStatic val main = DispatchQueue("main")

        @JvmStatic var global: DispatchQueue? = null

        fun createGlobalQueue() {
            if (this.global == null) {
                Thread {
                    this.global = DispatchQueue("global")
                    Looper.loop()
                }.start()
            }
        }

    }

}

fun KIMIPackage.installDispatchQueue() {
    exporter.exportClass(DispatchQueue::class.java, "DispatchQueue")
    exporter.exportMethodToJavaScript(DispatchQueue::class.java, "async")
    exporter.exportMethodToJavaScript(DispatchQueue::class.java, "asyncAfter")
    exporter.exportMethodToJavaScript(DispatchQueue::class.java, "_isolate")
    exporter.exportInitializer(DispatchQueue::class.java) {
        val identifier = it.firstOrNull() as? String ?: return@exportInitializer DispatchQueue.main
        if (identifier == "main") {
            return@exportInitializer DispatchQueue.main
        }
        return@exportInitializer DispatchQueue(identifier)
    }
    exporter.exportStaticProperty(DispatchQueue::class.java, "main", true)
    exporter.exportStaticProperty(DispatchQueue::class.java, "global", true)
    exporter.exportScript(DispatchQueue::class.java, "Initializer.prototype.isolate = function(){ var args = []; for(var i=1;i<arguments.length;i++){ args.push(arguments[i]); } this._isolate(arguments[0].toString(), args); }", true)
    DispatchQueue.createGlobalQueue()
}