package com.xt.kimi.uikit

import com.xt.endo.CGSize
import com.xt.kimi.KIMIPackage

class UIImageView: UIView() {

    var image: UIImage? = null
        set(value) {
            field = value
            this.layer.contents = value
            this.setNeedsDisplay()
        }

    override fun intrinsicContentSize(): CGSize? {
        image?.let {
            return it.size
        }
        return super.intrinsicContentSize()
    }

}

fun KIMIPackage.installUIImageView() {
    exporter.exportClass(UIImageView::class.java, "UIImageView", "UIView")
    exporter.exportProperty(UIImageView::class.java, "image")
}