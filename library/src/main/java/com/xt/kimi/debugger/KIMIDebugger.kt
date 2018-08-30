package com.xt.kimi.debugger

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.xt.endo.EDOExporter
import com.xt.jscore.JSContext
import com.xt.kimi.currentActivity
import com.xt.kimi.uikit.scale
import okhttp3.*
import java.io.IOException

class KIMIDebugger(val applicationContext: Context, remoteAddress: String? = null) {

    private var remoteAddress: String = remoteAddress ?: applicationContext.getSharedPreferences("com.xt.kimi", Context.MODE_PRIVATE)?.getString("debugger.address", null) ?: "10.0.2.2:8090"
    private var httpClient = OkHttpClient()
    private var closed = false
    private var lastTag: String? = null
    private var contextInitializer: (() -> JSContext)? = null

    fun setContextInitializer(value: () -> JSContext) {
        this.contextInitializer = value
    }

    fun connect(callback: (context: JSContext) -> Unit, fallback: () -> Unit) {
        val dialog = this.displayConnectingDialog(callback, fallback)
        this.httpClient.newCall(Request.Builder()
                .url("http://$remoteAddress/source")
                .get()
                .build()).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Handler(applicationContext.mainLooper).post {
                    dialog.hide()
                    fallback()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val script = response?.body()?.string() ?: return
                Handler(applicationContext.mainLooper).post {
                    val context = this@KIMIDebugger.contextInitializer?.invoke() ?: JSContext()
                    EDOExporter.sharedExporter.exportWithContext(context)
                    context.evaluateScript(script)
                    dialog.hide()
                    callback(context)
                    this@KIMIDebugger.fetchUpdate(callback)
                }
            }
        })
    }

    fun fetchUpdate(callback: (context: JSContext) -> Unit) {
        Handler(applicationContext.mainLooper).postDelayed({
            this.httpClient.newCall(Request.Builder()
                    .url("http://$remoteAddress/version")
                    .get()
                    .build()).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    this@KIMIDebugger.fetchUpdate(callback)
                }
                override fun onResponse(call: Call?, response: Response?) {
                    val tag = response?.body()?.string() ?: return this@KIMIDebugger.fetchUpdate(callback)
                    if (this@KIMIDebugger.lastTag == null) {
                        this@KIMIDebugger.lastTag = tag
                        this@KIMIDebugger.fetchUpdate(callback)
                    }
                    else if (this@KIMIDebugger.lastTag != tag) {
                        this@KIMIDebugger.lastTag = tag
                        Handler(applicationContext.mainLooper).post {
                            this@KIMIDebugger.connect(callback, {})
                        }
                    }
                    else {
                        this@KIMIDebugger.fetchUpdate(callback)
                    }
                }
            })
        }, 500)
    }

    private fun displayConnectingDialog(callback: (context: JSContext) -> Unit, fallback: () -> Unit): AlertDialog {
        val dialog = AlertDialog.Builder(applicationContext)
                .setTitle("KIMI Debugger")
                .setMessage("connecting to " + this.remoteAddress)
                .setNegativeButton("FORCE CLOSE") { _, _ ->
                    this.closed = true
                    fallback()
                }
                .setPositiveButton("MODIFY") { _, _ ->
                    this.displayModifyDialog(callback, fallback)
                }
                .create()
        dialog.show()
        return dialog
    }

    private fun displayModifyDialog(callback: (context: JSContext) -> Unit, fallback: () -> Unit) {
        val layout = RelativeLayout(currentActivity)
        val editText = EditText(currentActivity)
        editText.hint = "Input IP:Port Here"
        editText.text.append(this.remoteAddress)
        editText.setSingleLine()
        layout.setPadding((20 * scale).toInt(), 0, (20 * scale).toInt(), 0)
        layout.addView(editText, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        AlertDialog.Builder(currentActivity)
                .setMessage("Enter KIMI Debugger Address")
                .setView(layout)
                .setNegativeButton("Cancel") { _, _ ->
                    this.connect(callback, fallback)
                }
                .setPositiveButton("OK") { _, _ ->
                    this.remoteAddress = editText.text.toString()
                    this.connect(callback, fallback)
                }
                .show()
    }

}