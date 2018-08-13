package com.xt.kimi.uikit

import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.*
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
            attributes[UIAttributedStringKey.font.name]?.let { font ->
                (font as? UIFont)?.let {
                    font.fontName?.let {
                        spannableString.setSpan(TypefaceSpan(it), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    font.fontStyle?.let {
                        spannableString.setSpan(StyleSpan(kotlin.run {
                            when (it) {
                                "bold" -> return@run Typeface.BOLD
                                "heavy" -> return@run Typeface.BOLD
                                "black" -> return@run Typeface.BOLD
                                "italic" -> return@run Typeface.ITALIC
                                else -> return@run Typeface.NORMAL
                            }
                        }), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    spannableString.setSpan(AbsoluteSizeSpan((it.pointSize * scale).toInt(), true), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            attributes[UIAttributedStringKey.backgroundColor.name]?.let { backgroundColor ->
                (backgroundColor as? UIColor)?.let {
                    spannableString.setSpan(BackgroundColorSpan(it.toInt()), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            attributes[UIAttributedStringKey.kern.name]?.let { kern ->
                (kern as? Number)?.let {
                    spannableString.setSpan(object : CharacterStyle() {
                        override fun updateDrawState(textPaint: TextPaint) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textPaint.letterSpacing = (it.toDouble() / 16.0 * scale).toFloat()
                            }
                        }
                    }, targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            attributes[UIAttributedStringKey.strikethroughStyle.name]?.let {
                (it as? Int)?.takeIf { it == 1 }?.let {
                    spannableString.setSpan(StrikethroughSpan(), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            attributes[UIAttributedStringKey.underlineStyle.name]?.let {
                (it as? Int)?.takeIf { it == 1 }?.let {
                    spannableString.setSpan(UnderlineSpan(), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            attributes[UIAttributedStringKey.paragraphStyle.name]?.let {
                (it as? UIParagraphStyle)?.let {
                    spannableString.setSpan(AlignmentSpan.Standard(kotlin.run {
                        when (it.alignment) {
                            UITextAlignment.left -> return@run Layout.Alignment.ALIGN_NORMAL
                            UITextAlignment.center -> return@run Layout.Alignment.ALIGN_CENTER
                            UITextAlignment.right -> return@run Layout.Alignment.ALIGN_OPPOSITE
                        }
                    }), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                //todo
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