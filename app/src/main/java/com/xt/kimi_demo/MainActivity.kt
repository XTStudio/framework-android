package com.xt.kimi_demo

import android.app.Activity
import android.os.Bundle
import com.xt.endo.EDOFactory
import com.xt.jscore.JSContext
import com.xt.kimi.uikit.UIViewController

class MainActivity : Activity() {

    private var context: JSContext? = null

    private var main: UIViewController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.context = EDOFactory.decodeContextFromAssets("test.js", this, "192.168.0.23:8090") {
            this.context = it
            this.attachWindow()
        }
//        this.attachWindow()
    }

    private fun attachWindow() {
        val context = this.context ?: return
        (EDOFactory.objectFromContext(context) as? UIViewController)?.let {
            it.attachToActivity(this, true)
            this.main = it
        }
    }

    override fun onBackPressed() {
        if (this.main?.canGoBack() == true) {
            this.main?.goBack()
            return
        }
        super.onBackPressed()
    }

}
