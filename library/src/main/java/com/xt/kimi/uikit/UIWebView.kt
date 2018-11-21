package com.xt.kimi.uikit

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.*
import com.xt.endo.EDOCallback
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import com.xt.kimi.foundation.URL
import com.xt.kimi.foundation.URLRequest

private class UIWebViewJavaScriptInterface(val webView: UIWebView) {

    @JavascriptInterface
    fun bridgeScript(): String {
        return "var KIMICallbacks=[];KIMI.postMessage=function(body,callback){var callbackIdx=typeof callback===\"function\"?KIMICallbacks.length:-1;if(callbackIdx!==undefined){KIMICallbacks.push(callback)}KIMI._postMessage(body, callbackIdx)};KIMI.onMessage=function(callbackIdx,args){try{KIMICallbacks[callbackIdx].apply(undefined,args)}catch(error){}};"
    }

    @JavascriptInterface
    fun _postMessage(body: String, callbackIdx: Int) {
        this.webView.post {
            val value = EDOJavaHelper.value(this.webView, "message", body)
            this.webView.evaluateJavaScript("KIMI.onMessage($callbackIdx, [`$value`])", EDOCallback.createWithBlock {  })
        }
    }

}

open class UIWebView: UINativeTouchView() {

    var title: String? = null
        private set

    val URL: URL?
        get() {
            this.systemWebView.url?.let {
                return URL(Uri.parse(it))
            }
            return null
        }

    var loading: Boolean = false
        private set

    fun loadRequest(urlRequest: URLRequest) {
        this.systemWebView.loadUrl(urlRequest.URL.absoluteString)
    }

    fun loadHTMLString(HTMLString: String, baseURL: URL) {
        this.systemWebView.loadDataWithBaseURL(baseURL.absoluteString, HTMLString, null, null, null)
    }

    fun goBack() {
        this.systemWebView.goBack()
    }

    fun goForward() {
        this.systemWebView.goForward()
    }

    fun reload() {
        this.systemWebView.reload()
    }

    fun stopLoading() {
        this.systemWebView.stopLoading()
    }

    fun evaluateJavaScript(script: String, complete: EDOCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.systemWebView.evaluateJavascript(script) {
                complete?.invoke(it)
            }
        }
        else {
            this.systemWebView.loadUrl("javascript: $script")
            complete.invoke()
        }
    }

    // Implementation

    val systemWebView = WebView(this.context)

    init {
        this.resetWebViewConfiguration()
    }

    private fun resetWebViewConfiguration() {
        this.systemWebView.webChromeClient = object: WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                this@UIWebView.title = title
            }
        }
        this.systemWebView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                (EDOJavaHelper.value(this@UIWebView, "newRequest", URLRequest(URL(Uri.parse(url ?: "")))) as? Boolean)?.let {
                    return !it
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    (EDOJavaHelper.value(this@UIWebView, "newRequest", URLRequest(URL(request?.url ?: Uri.parse("")))) as? Boolean)?.let {
                        return !it
                    }
                }
                return false
            }

            var loadFailed = false

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url == "about:error") { return }
                this@UIWebView.loading = true
                EDOJavaHelper.emit(this@UIWebView, "didStart")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url == "about:error") { return }
                this@UIWebView.loading = false
                if (!loadFailed) {
                    EDOJavaHelper.emit(this@UIWebView, "didFinish")
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                loadFailed = true
                super.onReceivedError(view, errorCode, description, failingUrl)
                this@UIWebView.systemWebView.loadDataWithBaseURL("about:error", "", null, null, null)
                this@UIWebView.loading = false
                EDOJavaHelper.emit(this@UIWebView, "didFail", Error(description))
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                loadFailed = true
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this@UIWebView.systemWebView.loadDataWithBaseURL("about:error", "", null, null, null)
                }
                this@UIWebView.loading = false
                EDOJavaHelper.emit(this@UIWebView, "didFail", Error(error?.toString()))
            }
        }
        this.systemWebView.settings.javaScriptEnabled = true
        this.systemWebView.settings.databaseEnabled = true
        this.systemWebView.settings.domStorageEnabled = true
        this.systemWebView.settings.useWideViewPort = true
        this.systemWebView.addJavascriptInterface(UIWebViewJavaScriptInterface(this), "KIMI")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(kotlin.run {
                try {
                    val clazz = Class.forName(this.context.packageName + ".BuildConfig")
                    val field = clazz.getField("DEBUG")
                    if (field.get(clazz) as? Boolean == true) {
                        return@run true
                    }
                } catch (e: Exception) { }
                return@run false
            })
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            this.removeView(this.systemWebView)
            this.addView(this.systemWebView, ViewGroup.LayoutParams(this.width, this.height))
        }
    }

}

fun KIMIPackage.installUIWebView() {
    exporter.exportClass(UIWebView::class.java, "UIWebView", "UIView")
    exporter.exportProperty(UIWebView::class.java, "title", true)
    exporter.exportProperty(UIWebView::class.java, "URL", true)
    exporter.exportProperty(UIWebView::class.java, "loading", true)
    exporter.exportMethodToJavaScript(UIWebView::class.java, "loadRequest")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "loadHTMLString")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "goBack")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "goForward")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "reload")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "stopLoading")
    exporter.exportMethodToJavaScript(UIWebView::class.java, "evaluateJavaScript")
}