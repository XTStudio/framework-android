package com.xt.kimi_demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
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

        val testView = UIView()
        val testWindow = UIWindow()
        testView.frame = CGRect(0.0, 0.0, 100.0, 100.0)
        testView.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 44.0, 44.0)
        val yyyView = UIView()
        yyyView.frame = CGRect(200.0, 200.0, 44.0, 44.0)
        testWindow.addSubview(testView)
        testWindow.addSubview(yyyView)
        val e = yyyView.convertPointFromView(CGPoint(50.0, 50.0), testView)

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
