package com.xt.kimi.coregraphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder
import com.xt.endo.CGRect
import com.xt.kimi.uikit.*
import kotlin.math.ceil

class CATextLayer: CALayer() {

    internal var textLayout: Layout? = null

    override fun drawContent(ctx: Canvas) {
        super.drawContent(ctx)
        textLayout?.let {
            this.drawText(ctx)
            return
        }
        val view = this.view as? UILabel ?: return
        val builder = TextLayoutBuilder()
                .setText(view.attributedText?.spannableString ?: view.text ?: "")
                .setTextSize(((view.font?.pointSize ?: 17.0) * scale).toInt())
                .setWidth((this.frame.width * scale).toInt())
                .setTextColor(view.textColor?.toInt() ?: Color.BLACK)
                .setTextStyle(kotlin.run {
                    val fontStyle = view.font?.fontStyle ?: return@run Typeface.NORMAL
                    when (fontStyle) {
                        "bold" -> return@run Typeface.BOLD
                        "heavy" -> return@run Typeface.BOLD
                        "black" -> return@run Typeface.BOLD
                        "italic" -> return@run Typeface.ITALIC
                    }
                    return@run Typeface.NORMAL
                })
                .setAlignment(kotlin.run {
                    return@run when (view.textAlignment) {
                        UITextAlignment.left -> Layout.Alignment.ALIGN_NORMAL
                        UITextAlignment.center -> Layout.Alignment.ALIGN_CENTER
                        UITextAlignment.right -> Layout.Alignment.ALIGN_OPPOSITE
                    }
                })
                .setMaxLines(if (view.numberOfLines == 0) 999999 else view.numberOfLines)
                .setEllipsize(kotlin.run {
                    if (view.numberOfLines == 1) {
                        when (view.lineBreakMode) {
                            UILineBreakMode.truncatingHead -> return@run TextUtils.TruncateAt.START
                            UILineBreakMode.truncatingMiddle -> return@run TextUtils.TruncateAt.MIDDLE
                            UILineBreakMode.truncatingTail -> return@run TextUtils.TruncateAt.END
                        }
                    }
                    return@run TextUtils.TruncateAt.END
                })
        view.font?.fontName?.let { fontName ->
            builder.setTypeface(Typeface.create(fontName, kotlin.run {
                val fontStyle = view.font?.fontStyle ?: return@run Typeface.NORMAL
                when (fontStyle) {
                    "bold" -> return@run Typeface.BOLD
                    "heavy" -> return@run Typeface.BOLD
                    "black" -> return@run Typeface.BOLD
                    "italic" -> return@run Typeface.ITALIC
                }
                return@run Typeface.NORMAL
            }))
        }
        builder.build()?.let { layout ->
            if (layout.height > this.frame.height * scale) {
                var currentHeight = 0.0
                var limitLine = 0
                var bounds = Rect()
                while (currentHeight + bounds.height() <= this.frame.height * scale) {
                    layout.getLineBounds(limitLine, bounds)
                    currentHeight += bounds.height()
                    limitLine++
                }
                builder.maxLines = limitLine
                builder.build()?.let { textLayout = it }
            }
            else {
                textLayout = layout
            }
            this.drawText(ctx)
        }
    }

    fun drawText(ctx: Canvas) {
        textLayout?.let { layout ->
            val ty = (this.frame.height * scale - layout.height) / 2.0
            ctx.translate(0.0f, ty.toFloat())
            shadowColor?.let { shadowColor ->
                shadowOffset?.let { shadowOffset ->
                    if (shadowColor.a > 0 && shadowOpacity > 0 && shadowRadius > 0) {
                        val shadowColorWithOpacity = if (shadowOpacity < 1) UIColor(shadowColor.r, shadowColor.g, shadowColor.b, shadowColor.a * shadowOpacity) else shadowColor
                        layout.paint.setShadowLayer(
                                (shadowRadius * scale).toFloat(),
                                (shadowOffset.width * scale).toFloat(),
                                (shadowOffset.height * scale).toFloat(),
                                shadowColorWithOpacity.toInt()
                        )
                    }
                }
            }
            this.setAlphaForPaint(layout.paint, this)
            layout.draw(ctx)
            ctx.translate(0.0f, -ty.toFloat())
        }
    }

    fun textBounds(width: Double?): CGRect {
        val view = this.view as? UILabel ?: return CGRect(0.0, 0.0, 0.0, 0.0)
        view.attributedText?.spannableString?.let { spannableString ->
            val textWidth = Layout.getDesiredWidth(spannableString, TextPaint())
            if (textWidth < (width ?: Double.MAX_VALUE) * scale) {
                val builder = TextLayoutBuilder()
                        .setText(spannableString)
                        .setWidth(((width ?: Double.MAX_VALUE) * scale).toInt())
                return CGRect(0.0, 0.0, ceil(textWidth / scale + 1.0), (builder.build()?.height?.toDouble() ?: 0.0) / scale)
            }
            else {
                val builder = TextLayoutBuilder()
                        .setText(spannableString)
                        .setWidth(((width ?: Double.MAX_VALUE) * scale).toInt())
                builder.build()?.let {
                    return CGRect(0.0, 0.0, ((0 until it.lineCount).map { idx -> it.getLineRight(idx) }.maxBy { it }?.toDouble() ?: 0.0) / scale, it.height.toDouble() / scale)
                }
            }
        }
        val builder = TextLayoutBuilder()
                .setText(view.text ?: "")
                .setTextSize(((view.font?.pointSize ?: 17.0) * scale).toInt())
                .setWidth(((width ?: Double.MAX_VALUE) * scale).toInt())
                .setTextStyle(kotlin.run {
                    val fontStyle = view.font?.fontStyle ?: return@run Typeface.NORMAL
                    when (fontStyle) {
                        "bold" -> return@run Typeface.BOLD
                        "heavy" -> return@run Typeface.BOLD
                        "black" -> return@run Typeface.BOLD
                        "italic" -> return@run Typeface.ITALIC
                    }
                    return@run Typeface.NORMAL
                })
                .setAlignment(kotlin.run {
                    return@run when (view.textAlignment) {
                        UITextAlignment.left -> Layout.Alignment.ALIGN_NORMAL
                        UITextAlignment.center -> Layout.Alignment.ALIGN_CENTER
                        UITextAlignment.right -> Layout.Alignment.ALIGN_OPPOSITE
                    }
                })
                .setMaxLines(if (view.numberOfLines == 0) 999999 else view.numberOfLines)
                .setEllipsize(kotlin.run {
                    if (view.numberOfLines == 1) {
                        when (view.lineBreakMode) {
                            UILineBreakMode.truncatingHead -> return@run TextUtils.TruncateAt.START
                            UILineBreakMode.truncatingMiddle -> return@run TextUtils.TruncateAt.MIDDLE
                            UILineBreakMode.truncatingTail -> return@run TextUtils.TruncateAt.END
                        }
                    }
                    return@run TextUtils.TruncateAt.END
                })
        builder.build()?.let {
            return CGRect(0.0, 0.0, ((0 until it.lineCount).map { idx -> it.getLineRight(idx) }.maxBy { it }?.toDouble() ?: 0.0) / scale, it.height.toDouble() / scale)
        }
        return CGRect(0.0, 0.0, 0.0, 0.0)
    }

}