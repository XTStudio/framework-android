package com.xt.kimi.uikit

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.eclipsesource.v8.V8
import com.xt.endo.UIRange
import com.xt.kimi.KIMIPackage

enum class UIAttributedStringKey {
    foregroundColor,      // value: UIColor
    font,                 // value: UIFont
    backgroundColor,      // value: UIColor
    kern,                 // value: number
    strikethroughStyle,   // value: number
    underlineStyle,       // value: number
    strokeColor,          // value: UIColor
    strokeWidth,          // value: number
    underlineColor,       // value: UIColor
    strikethroughColor,   // value: UIColor
    paragraphStyle,       // value: NSParagraphStyle
}

class UIAttributedString(str: String, attributes: Map<String, Any>?) {

    internal val spannableString: SpannableString = SpannableString(str)

    val string: String
        get() {
            return this.spannableString.toString()
        }

    init {
        this.setAttributesToSpannableString(this.spannableString, null, attributes)
    }

    private fun setAttributesToSpannableString(spannableString: SpannableString, range: UIRange?, attributes: Map<String, Any>?) {
        val targetRange = range ?: UIRange(0, spannableString.length)
        attributes?.let { attributes ->
            attributes[UIAttributedStringKey.foregroundColor.name]?.let { foregroundColor ->
                (foregroundColor as? UIColor)?.let {
                    spannableString.setSpan(ForegroundColorSpan(it.toInt()), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

}

fun KIMIPackage.installUIAttributedString() {
    exporter.exportClass(UIAttributedString::class.java, "UIAttributedString")
    exporter.exportInitializer(UIAttributedString::class.java) {
        val str = it.getOrNull(0) as? String ?: return@exportInitializer V8.getUndefined()
        val attributes = it.getOrNull(1) as? Map<String, Any>
        return@exportInitializer UIAttributedString(str, attributes)
    }
    exporter.exportEnum("UIAttributedStringKey", mapOf(
            Pair("foregroundColor", UIAttributedStringKey.foregroundColor.name),
            Pair("font", UIAttributedStringKey.font.name),
            Pair("backgroundColor", UIAttributedStringKey.backgroundColor.name),
            Pair("kern", UIAttributedStringKey.kern.name),
            Pair("strikethroughStyle", UIAttributedStringKey.strikethroughStyle.name),
            Pair("underlineStyle", UIAttributedStringKey.underlineStyle.name),
            Pair("strokeColor", UIAttributedStringKey.strokeColor.name),
            Pair("strokeWidth", UIAttributedStringKey.strokeWidth.name),
            Pair("underlineColor", UIAttributedStringKey.underlineColor.name),
            Pair("strikethroughColor", UIAttributedStringKey.strikethroughColor.name),
            Pair("paragraphStyle", UIAttributedStringKey.paragraphStyle.name)
    ))
}