package com.xt.kimi.uikit

import android.text.SpannableString
import com.xt.endo.CGRect
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CALayer
import com.xt.kimi.coregraphics.CATextLayer

enum class UITextAlignment {
    left,
    center,
    right,
}

enum class UILineBreakMode {
    wordWrapping,
    charWrapping,
    clipping,
    truncatingHead,
    truncatingTail,
    truncatingMiddle,
}

class UILabel: UIView() {

    override val layer: CATextLayer = CATextLayer()

    override var frame: CGRect
        get() = super.frame
        set(value) {
            super.frame = value
            this.layer.textLayout = null
        }

    var edo_text: String? = null
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var font: UIFont? = UIFont(17.0)
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var textColor: UIColor? = null
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var textAlignment: UITextAlignment = UITextAlignment.left
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var lineBreakMode: UILineBreakMode = UILineBreakMode.truncatingTail
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var numberOfLines = 1
        set(value) {
            field = value
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

}

fun KIMIPackage.installUILabel() {
    exporter.exportClass(UILabel::class.java, "UILabel", "UIView")
    exporter.exportProperty(UILabel::class.java, "edo_text")
    exporter.exportProperty(UILabel::class.java, "font")
    exporter.exportProperty(UILabel::class.java, "textColor")
    exporter.exportProperty(UILabel::class.java, "textAlignment")
    exporter.exportProperty(UILabel::class.java, "lineBreakMode")
    exporter.exportProperty(UILabel::class.java, "numberOfLines")
    exporter.exportEnum("UITextAlignment", mapOf(
            Pair("left", UITextAlignment.left),
            Pair("center", UITextAlignment.center),
            Pair("right", UITextAlignment.right)
    ))
    exporter.exportEnum("UILineBreakMode", mapOf(
            Pair("wordWrapping", UILineBreakMode.wordWrapping),
            Pair("charWrapping", UILineBreakMode.charWrapping),
            Pair("clipping", UILineBreakMode.clipping),
            Pair("truncatingHead", UILineBreakMode.truncatingHead),
            Pair("truncatingTail", UILineBreakMode.truncatingTail),
            Pair("truncatingMiddle", UILineBreakMode.truncatingMiddle)
    ))
}