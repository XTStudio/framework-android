package com.xt.kimi.uikit

import android.graphics.PorterDuff
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.xt.kimi.KIMIPackage

class UIActivityIndicatorView: UIView() {

    var color: UIColor = this.tintColor ?: UIColor.black
        set(value) {
            field = value
            systemProgressBar.indeterminateDrawable.setColorFilter(value.toInt(), PorterDuff.Mode.MULTIPLY)
        }

    var largeStyle: Boolean = false
        set(value) {
            this.removeView(systemProgressBar)
            field = value
            systemProgressBar = ProgressBar(this.context, null, if (value) android.R.attr.progressBarStyleLarge else android.R.attr.progressBarStyle)
            systemProgressBar.isIndeterminate = true
            systemProgressBar.visibility = View.GONE
            addView(systemProgressBar, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            this.color = this.tintColor ?: UIColor.black
            this.requestFocus()
            val size = if (this.largeStyle) 76.0 else 44.0
            systemProgressBar.x = ((width - size * scale) / 2.0).toFloat()
            systemProgressBar.y = ((height - size * scale) / 2.0).toFloat()
        }

    var animating: Boolean = false
        private set

    fun startAnimating() {
        this.animating = true
        systemProgressBar.visibility = View.VISIBLE
    }

    fun stopAnimating() {
        this.animating = false
        systemProgressBar.visibility = View.GONE
    }

    // Implementation

    private var systemProgressBar = ProgressBar(this.context, null, android.R.attr.progressBarStyle)

    init {
        this.userInteractionEnabled = false
        systemProgressBar.isIndeterminate = true
        systemProgressBar.visibility = View.GONE
        addView(systemProgressBar, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        this.color = this.tintColor ?: UIColor.black
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            val size = if (this.largeStyle) 76.0 else 44.0
            systemProgressBar.x = ((width - size * scale) / 2.0).toFloat()
            systemProgressBar.y = ((height - size * scale) / 2.0).toFloat()
        }
    }

}

fun KIMIPackage.installUIActivityIndicatorView() {
    exporter.exportClass(UIActivityIndicatorView::class.java, "UIActivityIndicatorView", "UIView")
    exporter.exportProperty(UIActivityIndicatorView::class.java, "color")
    exporter.exportProperty(UIActivityIndicatorView::class.java, "largeStyle")
    exporter.exportProperty(UIActivityIndicatorView::class.java, "animating", true)
    exporter.exportMethodToJavaScript(UIActivityIndicatorView::class.java, "startAnimating")
    exporter.exportMethodToJavaScript(UIActivityIndicatorView::class.java, "stopAnimating")
}