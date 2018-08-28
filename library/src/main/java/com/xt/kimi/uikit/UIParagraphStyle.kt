package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UIParagraphStyle {

    /**
     * The distance in points between the bottom of one line fragment and the top of the next.
     */
    var lineSpacing: Double = 0.0

    /**
     * The text alignment of the receiver.
     */
    var alignment: UITextAlignment = UITextAlignment.left

    /**
     * The mode that should be used to break lines in the receiver.
     */
    var lineBreakMode: UILineBreakMode = UILineBreakMode.truncatingTail

    /**
     * The receiver’s minimum height.
     */
    var minimumLineHeight: Double = 0.0

    /**
     * The receiver’s maximum line height.
     */
    var maximumLineHeight: Double = 0.0

    /**
     * The line height multiple.
     */
    var lineHeightMultiple: Double = 0.0

}

fun KIMIPackage.installUIParagraphStyle() {
    exporter.exportClass(UIParagraphStyle::class.java, "UIParagraphStyle")
    exporter.exportProperty(UIParagraphStyle::class.java, "lineSpacing")
    exporter.exportProperty(UIParagraphStyle::class.java, "alignment")
    exporter.exportProperty(UIParagraphStyle::class.java, "lineBreakMode")
    exporter.exportProperty(UIParagraphStyle::class.java, "minimumLineHeight")
    exporter.exportProperty(UIParagraphStyle::class.java, "maximumLineHeight")
    exporter.exportProperty(UIParagraphStyle::class.java, "lineHeightMultiple")
}