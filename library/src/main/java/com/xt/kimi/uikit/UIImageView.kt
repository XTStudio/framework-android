package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UIImageView: UIView() {

    var image: UIImage? = null
        set(value) {
            field = value
            this.layer.contents = value
            this.setNeedsDisplay()
        }

}

fun KIMIPackage.installUIImageView() {
    exporter.exportClass(UIImageView::class.java, "UIImageView", "UIView")
    exporter.exportProperty(UIImageView::class.java, "image")
}