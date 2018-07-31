package com.xt.kimi.coregraphics

import android.graphics.*
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by cuiminghui on 2018/7/20.
 */

private val sharedOuterPath = Path()
private val sharedBackgroundPaint = Paint()

class CAOSCanvas(bitmap: Bitmap): Canvas(bitmap)

open class CALayer {

    internal var view: UIView? = null
        get() {
            return field ?: this.superlayer?.view
        }

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)

    var superlayer: CALayer? = null
        private set

    fun removeFromSuperlayer() {
        superlayer?.let { superlayer ->
            superlayer.sublayers = kotlin.run {
                val sublayers = superlayer.sublayers.toMutableList()
                sublayers.remove(this)
                return@run sublayers.toList()
            }
            superlayer.view?.setNeedsDisplay()
        }
    }

    var sublayers: List<CALayer> = listOf()
        private set

    fun addSublayer(layer: CALayer) {
        if (this == layer) { return }
        if (layer.superlayer != null) {
            layer.removeFromSuperlayer()
        }
        layer.superlayer = this
        sublayers = kotlin.run {
            val sublayers = this.sublayers.toMutableList()
            sublayers.add(layer)
            return@run sublayers.toList()
        }
        this.view?.setNeedsDisplay()
    }

    fun insertSublayerAtIndex(layer: CALayer, index: Int) {
        if (this == layer) { return }
        if (layer.superlayer != null) {
            layer.removeFromSuperlayer()
        }
        layer.superlayer = this
        sublayers = kotlin.run {
            val sublayers = this.sublayers.toMutableList()
            sublayers.add(index, layer)
            return@run sublayers.toList()
        }
        this.view?.setNeedsDisplay()
    }

    fun insertSublayerBelow(layer: CALayer, below: CALayer) {
        if (this == layer) { return }
        sublayers.indexOf(below)?.let { targetIndex ->
            if (layer.superlayer != null) {
                layer.removeFromSuperlayer()
            }
            layer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers.add(targetIndex, layer)
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    fun insertSublayerAbove(layer: CALayer, above: CALayer) {
        if (this == layer) { return }
        sublayers.indexOf(above)?.let { targetIndex ->
            if (layer.superlayer != null) {
                layer.removeFromSuperlayer()
            }
            layer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers.add(targetIndex + 1, layer)
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    fun replaceSublayer(oldLayer: CALayer, newLayer: CALayer) {
        if (this == oldLayer) { return }
        if (this == newLayer) { return }
        sublayers.indexOf(oldLayer)?.let { targetIndex ->
            if (newLayer.superlayer != null) {
                newLayer.removeFromSuperlayer()
            }
            newLayer.superlayer = this
            sublayers = kotlin.run {
                val sublayers = this.sublayers.toMutableList()
                sublayers[targetIndex] = newLayer
                return@run sublayers.toList()
            }
            this.view?.setNeedsDisplay()
        }
    }

    var hidden: Boolean = false
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var edo_mask: CALayer? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var masksToBounds: Boolean = false
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var backgroundColor: UIColor? = null

    var cornerRadius: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var borderWidth: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var borderColor: UIColor? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var opacity: Double = 1.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowColor: UIColor? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowOpacity: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowOffset: CGSize? = null
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var shadowRadius: Double = 0.0
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    open fun drawInContext(ctx: Canvas) {
        if (this.hidden) { return }
        ctx.save()
        this.edo_mask?.takeIf { ctx !is CAOSCanvas }?.let { maskLayer ->
            try {
                val contentBitmap = UIView.createBitmap((this.frame.width * scale).toInt(), (this.frame.height * scale).toInt())
                UIView.lockBitmap(contentBitmap)
                val offScreenCtx0 = CAOSCanvas(contentBitmap)
                this.drawInContext(offScreenCtx0)
                UIView.unlockBitmap(contentBitmap)
                val maskBitmap = UIView.createBitmap((this.frame.width * scale).toInt(), (this.frame.height * scale).toInt())
                UIView.lockBitmap(maskBitmap)
                val offScreenCtx1 = CAOSCanvas(maskBitmap)
                offScreenCtx1.translate((maskLayer.frame.x * scale).toFloat(), (maskLayer.frame.y * scale).toFloat())
                maskLayer.drawInContext(offScreenCtx1)
                UIView.unlockBitmap(maskBitmap)
                val concatBitmap = UIView.createBitmap((this.frame.width * scale).toInt(), (this.frame.height * scale).toInt())
                UIView.lockBitmap(concatBitmap)
                val concatCanvas = CAOSCanvas(concatBitmap)
                CALayer.sharedMaskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                concatCanvas.drawBitmap(contentBitmap, 0f, 0f, null)
                concatCanvas.drawBitmap(maskBitmap, 0f, 0f, CALayer.sharedMaskPaint)
                ctx.drawBitmap(concatBitmap, 0f, 0f, null)
                UIView.unlockBitmap(concatBitmap)
            } catch (e: Exception) { } // avoid OOM crash.
            return
        }
        this.drawShadow(ctx)
        if (this.masksToBounds) {
            ctx.clipPath(createBoundsPath())
        }
        if (this.backgroundColor != null) {
            ctx.drawPath(createBoundsPath(), createBackgroundPaint())
        }
        this.drawContent(ctx)
        this.sublayers.forEach {
            ctx.save()
            ctx.translate((it.frame.x * scale).toFloat(), (it.frame.y * scale).toFloat())
            it.drawInContext(ctx)
            ctx.restore()
        }
        ctx.restore()
        if (this.borderWidth != 0.0 && this.borderColor != null) {
            ctx.drawPath(createBorderPath(), createBorderPaint())
        }
    }

    internal fun createBoundsPath(): Path {
        sharedOuterPath.reset()
        val outRect = RectF((max(0.0, this.borderWidth) * scale).toFloat(), (max(0.0, this.borderWidth) * scale).toFloat(), ((this.frame.width - max(0.0, this.borderWidth)) * scale).toFloat(), ((this.frame.height - max(0.0, this.borderWidth)) * scale).toFloat())
        sharedOuterPath.addRoundRect(outRect, (this.cornerRadius * scale).toFloat(), (this.cornerRadius * scale).toFloat(), Path.Direction.CCW)
        return sharedOuterPath
    }

    private fun createBackgroundPaint(): Paint {
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.isAntiAlias = true
        sharedBackgroundPaint.color = Color.TRANSPARENT
        backgroundColor?.let {
            sharedBackgroundPaint.color = it.toInt()
        }
        this.setAlphaForPaint(sharedBackgroundPaint, this)
        return sharedBackgroundPaint
    }

    private fun createBorderPath(): Path {
        sharedOuterPath.reset()
        val outRect = RectF((this.borderWidth / 2.0 * scale).toFloat(), (this.borderWidth / 2.0 * scale).toFloat(), ((this.frame.width - this.borderWidth / 2.0) * scale).toFloat(), ((this.frame.width - this.borderWidth / 2.0) * scale).toFloat())
        val radiusRatio = this.cornerRadius / max(this.frame.width, this.frame.height)
        val newCornerRadius = max(outRect.width(), outRect.height()) * radiusRatio
        sharedOuterPath.addRoundRect(outRect, newCornerRadius.toFloat(), newCornerRadius.toFloat(), Path.Direction.CCW)
        return sharedOuterPath
    }

    private fun createBorderPaint(): Paint {
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.style = Paint.Style.STROKE
        sharedBackgroundPaint.strokeWidth = (abs(this.borderWidth) * scale).toFloat()
        sharedBackgroundPaint.isAntiAlias = true
        this.borderColor?.let {
            sharedBackgroundPaint.color = it.toInt()
        }
        this.setAlphaForPaint(sharedBackgroundPaint, this)
        return sharedBackgroundPaint
    }

    private fun drawShadow(ctx: Canvas, boundsPath: Path = createBorderPath()) {
        val shadowColor = shadowColor ?: return
        val shadowOffset = shadowOffset ?: return
        if (shadowColor.a > 0 && shadowOpacity > 0 && shadowRadius > 0) {
            try {
                val width = (this.frame.width + shadowRadius * 2) * scale
                val height = (this.frame.height + shadowRadius * 2) * scale
                val shadowBitmap = UIView.createBitmap(width.toInt(), height.toInt())
                UIView.lockBitmap(shadowBitmap)
                val canvas = CAOSCanvas(shadowBitmap)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                val paint = Paint()
                shadowColor?.let {
                    paint.color = it.toInt()
                }
                paint.alpha = (shadowOpacity * 255).toInt()
                paint.maskFilter = BlurMaskFilter((shadowRadius * scale).toFloat(), BlurMaskFilter.Blur.NORMAL)
                canvas.translate((shadowRadius * scale).toFloat(), (shadowRadius * scale).toFloat())
                canvas.drawPath(boundsPath, paint)
                sharedBackgroundPaint.reset()
                this.setAlphaForPaint(sharedBackgroundPaint, this)
                ctx.drawBitmap(
                        shadowBitmap,
                        ((-shadowRadius + shadowOffset.width) * scale).toFloat(),
                        ((-shadowRadius + shadowOffset.height) * scale).toFloat(),
                        sharedBackgroundPaint
                )
                UIView.unlockBitmap(shadowBitmap)
            } catch (e: Exception) { } // avoid OOM
        }
    }

    internal var contents: Any? = null

    protected open fun drawContent(ctx: Canvas) {
        val contentMode = this.view?.contentMode ?: return
        (contents as? UIImage)?.let {
            sharedContentPaint.reset()
            sharedContentPaint.isAntiAlias = true
            sharedContentPaint.isFilterBitmap = true
            this.setAlphaForPaint(sharedContentPaint, this)
            if (it.renderingMode == UIImageRenderingMode.alwaysTemplate) {
                this.view?.tintColor?.let { tintColor ->
                    sharedContentPaint.colorFilter = PorterDuffColorFilter(tintColor.toInt(), PorterDuff.Mode.SRC_IN)
                }
            }
            when (contentMode) {
                UIViewContentMode.scaleToFill -> {
                    ctx.drawBitmap(
                            it.bitmap,
                            Rect(0, 0, it.bitmap.width, it.bitmap.height),
                            RectF(0f, 0f, (this.frame.width * scale).toFloat(), (this.frame.height * scale).toFloat()),
                            sharedContentPaint
                    )
                }
                UIViewContentMode.scaleAspectFit -> {
                    val viewRatio = this.frame.width / this.frame.height
                    val contentRatio = it.bitmap.width / it.bitmap.height
                    if (viewRatio > contentRatio) {
                        val width = (it.bitmap.width * (this.frame.height / it.bitmap.height)).toFloat()
                        ctx.drawBitmap(
                                it.bitmap,
                                Rect(0, 0, it.bitmap.width, it.bitmap.height),
                                RectF(
                                        ((this.frame.width - width) / 2.0 * scale).toFloat(),
                                        0f,
                                        ((this.frame.width - width) / 2.0 * scale).toFloat() + width * scale,
                                        (this.frame.height * scale).toFloat()
                                ),
                                sharedContentPaint
                        )
                    }
                    else {
                        val height = (it.bitmap.height * (this.frame.width / it.bitmap.width)).toFloat()
                        ctx.drawBitmap(
                                it.bitmap,
                                Rect(0, 0, it.bitmap.width, it.bitmap.height),
                                RectF(
                                        0f,
                                        ((this.frame.height - height) / 2.0 * scale).toFloat(),
                                        (this.frame.width * scale).toFloat(),
                                        ((this.frame.height - height) / 2.0 * scale).toFloat() + height * scale
                                ),
                                sharedContentPaint
                        )
                    }
                }
                UIViewContentMode.scaleAspectFill -> {
                    val viewRatio = this.frame.width / this.frame.height
                    val contentRatio = it.bitmap.width / it.bitmap.height
                    if (viewRatio < contentRatio) {
                        val width = (it.bitmap.width * (this.frame.height / it.bitmap.height)).toFloat()
                        ctx.drawBitmap(
                                it.bitmap,
                                Rect(0, 0, it.bitmap.width, it.bitmap.height),
                                RectF(
                                        ((this.frame.width - width) / 2.0 * scale).toFloat(),
                                        0f,
                                        ((this.frame.width - width) / 2.0 * scale).toFloat() + width * scale,
                                        (this.frame.height * scale).toFloat()
                                ),
                                sharedContentPaint
                        )
                    }
                    else {
                        val height = (it.bitmap.height * (this.frame.width / it.bitmap.width)).toFloat()
                        ctx.drawBitmap(
                                it.bitmap,
                                Rect(0, 0, it.bitmap.width, it.bitmap.height),
                                RectF(
                                        0f,
                                        ((this.frame.height - height) / 2.0 * scale).toFloat(),
                                        (this.frame.width * scale).toFloat(),
                                        ((this.frame.height - height) / 2.0 * scale).toFloat() + height * scale
                                ),
                                sharedContentPaint
                        )
                    }
                }
            }
        }
    }

    protected fun setAlphaForPaint(paint: Paint, layer: CALayer) {
        paint.alpha = kotlin.run {
            var current: CALayer? = layer
            var opacity = paint.alpha / 255.0
            while (current != null) {
                opacity *= current!!.opacity
                current = current!!.superlayer
            }
            return@run max(0, min(255, (opacity * 255.0).toInt()))
        }
    }

    companion object {

        val sharedMaskPaint = Paint()

        val sharedContentPaint = Paint()

    }

}

fun KIMIPackage.installCALayer() {
    exporter.exportClass(CALayer::class.java, "CALayer")
    exporter.exportProperty(CALayer::class.java, "frame")
    exporter.exportProperty(CALayer::class.java, "superlayer", true)
    exporter.exportMethodToJavaScript(CALayer::class.java, "removeFromSuperlayer")
    exporter.exportProperty(CALayer::class.java, "sublayers", true)
    exporter.exportMethodToJavaScript(CALayer::class.java, "addSublayer")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerAtIndex")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerBelow")
    exporter.exportMethodToJavaScript(CALayer::class.java, "insertSublayerAbove")
    exporter.exportMethodToJavaScript(CALayer::class.java, "replaceSublayer")
    exporter.exportProperty(CALayer::class.java, "hidden")
    exporter.exportProperty(CALayer::class.java, "opacity")
    exporter.exportProperty(CALayer::class.java, "edo_mask")
    exporter.exportProperty(CALayer::class.java, "masksToBounds")
    exporter.exportProperty(CALayer::class.java, "backgroundColor")
    exporter.exportProperty(CALayer::class.java, "cornerRadius")
    exporter.exportProperty(CALayer::class.java, "borderWidth")
    exporter.exportProperty(CALayer::class.java, "borderColor")
    exporter.exportProperty(CALayer::class.java, "shadowColor")
    exporter.exportProperty(CALayer::class.java, "shadowOpacity")
    exporter.exportProperty(CALayer::class.java, "shadowOffset")
    exporter.exportProperty(CALayer::class.java, "shadowRadius")
}