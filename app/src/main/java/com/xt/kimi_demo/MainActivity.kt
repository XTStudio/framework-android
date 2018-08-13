package com.xt.kimi_demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.xt.endo.EDOExporter
import com.xt.endo.EDOObjectTransfer
import com.xt.jscore.JSContext
import com.xt.jscore.JSValue
import com.xt.kimi.uikit.UINavigationController
import com.xt.uulog.UULog

class MainActivity : Activity() {

    private val context = JSContext()
    private var main: UINavigationController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        UULog.attachTo(context)
        EDOExporter.sharedExporter.exportWithContext(context)
        context.exceptionHandler = { _, exception ->
            Log.e("JSContext", exception.toString())
        }
        this.loadScript()
    }

    private fun loadScript() {
        val inputStream = this.assets.open("test.js")
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val script = String(buffer)
        context.evaluateScript(script)
        val mainValue = context["main"] as JSValue
        val main = EDOObjectTransfer.convertToJavaObjectWithJSValue(mainValue, mainValue, null) as? UINavigationController
        main?.attachToActivity(this, true)
        this.main = main
        Log.d("MainActivity", main.toString())
    }

    override fun onBackPressed() {
        if (this.main?.canGoBack() == true) {
            this.main?.goBack()
            return
        }
        super.onBackPressed()
    }

}
