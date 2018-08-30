package com.xt.kimi_demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.xt.endo.EDOExporter
import com.xt.endo.EDOObjectTransfer
import com.xt.jscore.JSContext
import com.xt.jscore.JSValue
import com.xt.kimi.debugger.KIMIDebugger
import com.xt.kimi.uikit.UINavigationController
import com.xt.kimi.uikit.UIViewController
import com.xt.uulog.UULog

class MainActivity : Activity() {

    private var context: JSContext? = null

    private var main: UIViewController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val debugger = KIMIDebugger(this)
        debugger.setContextInitializer {
            val context = JSContext()
            context.evaluateScript("var main = undefined")
            return@setContextInitializer context
        }
        debugger.connect({
            this.context = it
            this.attachWindow()
        }, {
            val context = JSContext()
            UULog.attachTo(context)
            EDOExporter.sharedExporter.exportWithContext(context)
            context.exceptionHandler = { _, exception ->
                Log.e("JSContext", exception.toString())
            }
            this.context = context
            this.loadScript()
            this.attachWindow()
        })
    }

    private fun loadScript() {
        val context = this.context ?: return
        val inputStream = this.assets.open("test.js")
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val script = String(buffer)
        context.evaluateScript(script)
    }

    private fun attachWindow() {
        val context = this.context ?: return
        val mainValue = context["main"] as? JSValue ?: return
        val main = EDOObjectTransfer.convertToJavaObjectWithJSValue(mainValue, mainValue, null) as? UIViewController
        main?.attachToActivity(this, true)
        this.main = main
    }

    override fun onBackPressed() {
        if (this.main?.canGoBack() == true) {
            this.main?.goBack()
            return
        }
        super.onBackPressed()
    }

}
