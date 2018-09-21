package com.xt.kimi.uikit

import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UIParagraphStyle {

    /**
     * The distance in points between the bottom of one line fragment and the top of the next.
     */
    var lineSpacing: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineSpacing")
        }

    /**
     * The text alignment of the receiver.
     */
    var alignment: UITextAlignment = UITextAlignment.left
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "alignment")
        }

    /**
     * The mode that should be used to break lines in the receiver.
     */
    var lineBreakMode: UILineBreakMode = UILineBreakMode.truncatingTail
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineBreakMode")
        }

    /**
     * The receiver’s minimum height.
     */
    var minimumLineHeight: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "minimumLineHeight")
        }

    /**
     * The receiver’s maximum line height.
     */
    var maximumLineHeight: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "maximumLineHeight")
        }

    /**
     * The line height multiple.
     */
    var lineHeightMultiple: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineHeightMultiple")
        }

}

fun KIMIPackage.installUIParagraphStyle() {
    exporter.exportClass(UIParagraphStyle::class.java, "UIParagraphStyle")
    exporter.exportProperty(UIParagraphStyle::class.java, "lineSpacing", false, true, true)
    exporter.exportProperty(UIParagraphStyle::class.java, "alignment", false, true, true)
    exporter.exportProperty(UIParagraphStyle::class.java, "lineBreakMode", false, true, true)
    exporter.exportProperty(UIParagraphStyle::class.java, "minimumLineHeight", false, true, true)
    exporter.exportProperty(UIParagraphStyle::class.java, "maximumLineHeight", false, true, true)
    exporter.exportProperty(UIParagraphStyle::class.java, "lineHeightMultiple", false, true, true)
}