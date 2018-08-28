package com.xt.kimi.uikit

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.*
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.UIRange
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CATextLayer
import kotlin.math.min
import android.text.TextPaint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.text.style.LineHeightSpan



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

open class UIAttributedString(str: String, attributes: Map<String, Any>?) {

    open internal val spannableString: CharSequence = SpannableString(str)

    val string: String
        get() {
            return this.spannableString.toString()
        }

    init {
        (this.spannableString as? SpannableString)?.let {
            this.setAttributesToSpannableString(it, null, attributes)
        }
    }

    protected fun setAttributesToSpannableString(spannableString: SpannableString, range: UIRange?, attributes: Map<String, Any>?) {
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
                    (it.minimumLineHeight?.takeIf { it > 0 } ?: it.maximumLineHeight?.takeIf { it > 0 })?.let {
                        spannableString.setSpan(UILineHeightSpan((it * scale).toInt()), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                //todo
            }
        }
    }

    open fun measure(inSize: CGSize): CGRect {
        measureLabel.attributedText = this
        measureLabel.numberOfLines = 0
        measureLabel.layer.view = measureLabel
        val textBounds = measureLabel.layer.textBounds(inSize.width)
        return CGRect(0.0, 0.0, textBounds.width, min(inSize.height, textBounds.height))
    }

    companion object {

        private val measureLabel = UILabel()

    }

}

class UIMutableAttributedString(str: String, attributes: Map<String, Any>?): UIAttributedString(str, attributes) {

    private val builder = SpannableStringBuilder()

    override val spannableString: CharSequence
        get() {
            return this.builder
        }

    init {
        val spannableString = SpannableString(str)
        this.setAttributesToSpannableString(spannableString, null, attributes)
        this.builder.append(spannableString)
    }

    protected fun setAttributesToSpannableString(spannableString: SpannableStringBuilder, range: UIRange?, attributes: Map<String, Any>?) {
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
                    (it.minimumLineHeight?.takeIf { it > 0 } ?: it.maximumLineHeight?.takeIf { it > 0 })?.let {
                        spannableString.setSpan(UILineHeightSpan((it * scale).toInt()), targetRange.location, targetRange.location + targetRange.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                //todo
            }
        }
    }

    fun replaceCharacters(inRange: UIRange, withString: String) {
        this.builder.delete(inRange.location, inRange.location + inRange.length)
        this.builder.insert(inRange.location, withString)
    }

    fun setAttributes(attributes: Map<String, Any>, range: UIRange) {
        this.setAttributesToSpannableString(this.builder, range, attributes)
    }

    fun addAttribute(attrName: String, value: Any, range: UIRange) {
        this.setAttributesToSpannableString(this.builder, range, mapOf(Pair(attrName, value)))
    }

    fun addAttributes(attributes: Map<String, Any>, range: UIRange) {
        this.setAttributesToSpannableString(this.builder, range, attributes)
    }

    fun removeAttribute(attrName: String, range: UIRange) {}

    fun replaceCharactersWithAttributedString(inRange: UIRange, withAttributedString: UIAttributedString) {
        this.builder.delete(inRange.location, inRange.location + inRange.length)
        this.builder.insert(inRange.location, withAttributedString.spannableString)
    }

    fun insertAttributedString(attributedString: UIAttributedString, atIndex: Int) {
        this.builder.insert(atIndex, attributedString.spannableString)
    }

    fun appendAttributedString(attributedString: UIAttributedString) {
        this.builder.append(attributedString.spannableString)
    }

    fun deleteCharacters(inRange: UIRange) {
        this.builder.delete(inRange.location, inRange.location + inRange.length)
    }

    fun immutable(): UIAttributedString {
        return this
    }

    override fun measure(inSize: CGSize): CGRect {
        measureLabel.attributedText = this
        measureLabel.numberOfLines = 0
        measureLabel.layer.view = measureLabel
        val textBounds = measureLabel.layer.textBounds(inSize.width)
        return CGRect(0.0, 0.0, textBounds.width, min(inSize.height, textBounds.height))
    }

    companion object {

        private val measureLabel = UILabel()

    }

}

private class UILineHeightSpan(private val mSize: Int) : LineHeightSpan.WithDensity {

    override fun chooseHeight(text: CharSequence?, start: Int, end: Int, spanstartv: Int, v: Int, fm: FontMetricsInt?) {
        chooseHeight(text, start, end, spanstartv, v, fm, null);
    }

    override fun chooseHeight(text: CharSequence?, start: Int, end: Int,
                                spanstartv: Int, v: Int,
                                fm: Paint.FontMetricsInt?, paint: TextPaint?) {
        val fm = fm ?: return
        var size = mSize
        if (paint != null) {
            size *= paint.density.toInt()
        }
        if (fm.bottom - fm.top < size) {
            fm.top = fm.bottom - size
            fm.ascent = fm.ascent - size
        } else {
            if (sProportion == 0f) {
                val p = Paint()
                p.textSize = 100f
                val r = Rect()
                p.getTextBounds("ABCDEFG", 0, 7, r)

                sProportion = r.top / p.ascent()
            }
            val need = Math.ceil((-fm.top * sProportion).toDouble()).toInt()
            if (size - fm.descent >= need) {
                fm.top = fm.bottom - size
                fm.ascent = fm.descent - size
            } else if (size >= need) {
                fm.ascent = -need
                fm.top = fm.ascent
                fm.descent = fm.top + size
                fm.bottom = fm.descent
            } else {
                fm.ascent = -size
                fm.top = fm.ascent
                fm.descent = 0
                fm.bottom = fm.descent
            }
        }
    }

    companion object {
        private var sProportion = 0f
    }

}

fun KIMIPackage.installUIAttributedString() {
    exporter.exportClass(UIAttributedString::class.java, "UIAttributedString")
    exporter.exportInitializer(UIAttributedString::class.java) {
        val str = it.getOrNull(0) as? String ?: return@exportInitializer null
        val attributes = it.getOrNull(1) as? Map<String, Any>
        return@exportInitializer UIAttributedString(str, attributes)
    }
    exporter.exportMethodToJavaScript(UIAttributedString::class.java, "measure")
    exporter.exportClass(UIMutableAttributedString::class.java, "UIMutableAttributedString", "UIAttributedString")
    exporter.exportInitializer(UIMutableAttributedString::class.java) {
        try {
            val str = it.getOrNull(0) as? String ?: return@exportInitializer null
            val attributes = it.getOrNull(1) as? Map<String, Any>
            return@exportInitializer UIMutableAttributedString(str, attributes)
        } catch (e: Exception) {
            throw e
        }
    }
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "replaceCharacters")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "setAttributes")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "addAttribute")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "addAttributes")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "removeAttribute")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "replaceCharactersWithAttributedString")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "insertAttributedString")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "appendAttributedString")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "deleteCharacters")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "immutable")
    exporter.exportMethodToJavaScript(UIMutableAttributedString::class.java, "measure")
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