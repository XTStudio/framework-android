package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
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

    var text: String? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "text")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var attributedText: UIAttributedString? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "attributedText")
            value?.let {
                this.text = it.string
            }
        }

    var font: UIFont? = UIFont(17.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "font")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var textColor: UIColor? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "textColor")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var textAlignment: UITextAlignment = UITextAlignment.left
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "textAlignment")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var lineBreakMode: UILineBreakMode = UILineBreakMode.truncatingTail
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineBreakMode")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    var numberOfLines = 1
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "numberOfLines")
            this.layer.textLayout = null
            this.setNeedsDisplay()
        }

    init {
        this.userInteractionEnabled = false
    }

    override fun intrinsicContentSize(): CGSize? {
        if (this.text.isNullOrEmpty()) {
            return CGSize(0.0, 0.0)
        }
        this.layer.view = this
        val bounds = this.layer.textBounds(null)
        return CGSize(bounds.width, bounds.height)
    }

}

fun KIMIPackage.installUILabel() {
    exporter.exportClass(UILabel::class.java, "UILabel", "UIView")
    exporter.exportProperty(UILabel::class.java, "text", false, true, true)
    exporter.exportProperty(UILabel::class.java, "attributedText", false, true, true)
    exporter.exportProperty(UILabel::class.java, "font", false, true, true)
    exporter.exportProperty(UILabel::class.java, "textColor", false, true, true)
    exporter.exportProperty(UILabel::class.java, "textAlignment", false, true, true)
    exporter.exportProperty(UILabel::class.java, "lineBreakMode", false, true, true)
    exporter.exportProperty(UILabel::class.java, "numberOfLines", false, true, true)
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