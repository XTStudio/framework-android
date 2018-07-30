package com.xt.kimi_demo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.xt.endo.*
import com.xt.jscore.JSContext
import com.xt.jscore.JSValue
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIView
import com.xt.kimi.uikit.UIWindow
import com.xt.uulog.UULog
import java.io.File

class MainActivity : Activity() {

    private val context = JSContext()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
        val main = EDOObjectTransfer.convertToJavaObjectWithJSValue(mainValue, mainValue, null) as? UIView
        main?.attachToActivity(this)
        Log.d("MainActivity", main.toString())
    }

}
