package com.xt.kimi.uikit

import com.xt.endo.CGPoint
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UIFetchMoreControl: UIView() {

    internal var scrollView: UIScrollView? = null

    init {
        this.tintColor = UIColor.gray
    }

    var edo_enabled: Boolean = true

    var fetching: Boolean = false
        private set

    override fun tintColorDidChange() {
        super.tintColorDidChange()
    }

    fun beginFetching() {
        this.fetching = true
        EDOJavaHelper.emit(this, "fetch", this)
    }

    fun endFetching() {
        this.scrollView?.let {
            if (it.edo_contentOffset.y > it.contentSize.height + it.contentInset.bottom - it.bounds.height) {
                it.setContentOffset(CGPoint(0.0, it.contentSize.height + it.contentInset.bottom - it.bounds.height), true)
            }
        }
        this.fetching = false
    }

}

fun KIMIPackage.installUIFetchMoreControl() {
    exporter.exportClass(UIFetchMoreControl::class.java, "UIFetchMoreControl", "UIView")
    exporter.exportProperty(UIFetchMoreControl::class.java, "edo_enabled")
    exporter.exportProperty(UIFetchMoreControl::class.java, "fetching", true)
    exporter.exportMethodToJavaScript(UIFetchMoreControl::class.java, "beginFetching")
    exporter.exportMethodToJavaScript(UIFetchMoreControl::class.java, "endFetching")
}