package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.xt.endo.CGSize
import com.xt.kimi.KIMIPackage

class UIImageView: UIView() {

    var image: UIImage? = null
        set(value) {
            this.simpleDraweeView?.setImageURI(null as? String)
            field = value
            this.layer.contents = value
            this.setNeedsDisplay()
        }

    init {
        this.userInteractionEnabled = false
    }

    override val edo_isOpaque: Boolean
        get() {
            return this.image?.bitmap?.hasAlpha() == true && super.edo_isOpaque
        }

    private var simpleDraweeView: SimpleDraweeView? = null

    override var contentMode: UIViewContentMode
        get() = super.contentMode
        set(value) {
            super.contentMode = value
            this.simpleDraweeView?.let {
                when (value) {
                    UIViewContentMode.scaleAspectFit -> {
                        it.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
                    }
                    UIViewContentMode.scaleToFill -> {
                        it.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_XY
                    }
                    UIViewContentMode.scaleAspectFill -> {
                        it.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
                    }
                }
            }
        }

    override fun intrinsicContentSize(): CGSize? {
        image?.let {
            return it.size
        }
        return super.intrinsicContentSize()
    }

    fun loadImageWithURLString(URLString: String?, placeholder: UIImage?) {
        if (simpleDraweeView == null) {
            simpleDraweeView = SimpleDraweeView(this.context)
            this.contentMode = this.contentMode
            addView(simpleDraweeView, ViewGroup.LayoutParams((this.bounds.width * com.xt.kimi.uikit.scale).toInt(), (this.bounds.height * com.xt.kimi.uikit.scale).toInt()))
        }
        this.image = placeholder
        this.simpleDraweeView?.setImageURI(URLString)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.simpleDraweeView?.let {
            this.removeView(it)
            this.addView(it, ViewGroup.LayoutParams((this.bounds.width * com.xt.kimi.uikit.scale).toInt(), (this.bounds.height * com.xt.kimi.uikit.scale).toInt()))
        }
    }

    override fun draw(canvas: Canvas?) {
        if (UIRenderingOptimizer.shared.noNeedToDrawContent[this] == true) {
            this.simpleDraweeView?.visibility = View.GONE
        }
        else {
            this.simpleDraweeView?.visibility = View.VISIBLE
        }
        super.draw(canvas)
    }

}

fun KIMIPackage.installUIImageView() {
    Fresco.initialize(exporter.applicationContext)
    exporter.exportClass(UIImageView::class.java, "UIImageView", "UIView")
    exporter.exportProperty(UIImageView::class.java, "image")
    exporter.exportMethodToJavaScript(UIImageView::class.java, "loadImageWithURLString")
}