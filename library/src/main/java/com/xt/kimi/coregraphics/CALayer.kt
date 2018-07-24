package com.xt.kimi.coregraphics

import android.graphics.*
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.UIView
import com.xt.kimi.uikit.scale
import kotlin.math.max
import kotlin.math.min

/**
 * Created by cuiminghui on 2018/7/20.
 */

private val sharedOuterPath = Path()
private val sharedBackgroundPaint = Paint()

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

    var mask: CALayer? = null

    var masksToBounds: Boolean = false

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

    var shadowOpacity: Double = 0.0

    var shadowOffset: CGSize? = null

    var shadowRadius: Double = 0.0

    open fun drawInContext(ctx: Canvas) {
        if (this.hidden) { return }
        sharedOuterPath.reset()
        if (this.cornerRadius > 0) {
            sharedOuterPath.addRoundRect(RectF(0.0f, 0.0f, this.frame.width.toFloat() * scale, this.frame.height.toFloat() * scale), (this.cornerRadius * scale).toFloat(), (this.cornerRadius * scale).toFloat(), Path.Direction.CCW)
        }
        else {
            sharedOuterPath.addRect(RectF(0.0f, 0.0f, this.frame.width.toFloat() * scale, this.frame.height.toFloat() * scale), Path.Direction.CCW)
        }
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.isAntiAlias = true
        sharedBackgroundPaint.color = Color.TRANSPARENT
        backgroundColor?.let {
            sharedBackgroundPaint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
        }
        sharedBackgroundPaint.alpha = kotlin.run {
            var current: CALayer? = this
            var opacity = 1.0
            while (current != null) {
                opacity *= current.opacity
                current = current.superlayer
            }
            return@run max(0, min(255, (opacity * 255.0).toInt()))
        }
        ctx.drawPath(sharedOuterPath, sharedBackgroundPaint)
        if (this.borderWidth > 0 && this.borderColor != null) {
            sharedBackgroundPaint.style = Paint.Style.STROKE
            sharedBackgroundPaint.strokeWidth = (this.borderWidth * scale).toFloat()
            this.borderColor?.let {
                sharedBackgroundPaint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
            }
            ctx.drawPath(sharedOuterPath, sharedBackgroundPaint)
        }
        this.sublayers.forEach {
            ctx.save()
            ctx.translate((it.frame.x * scale).toFloat(), (it.frame.y * scale).toFloat())
            it.drawInContext(ctx)
            ctx.restore()
        }
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
    exporter.exportProperty(CALayer::class.java, "backgroundColor")
    exporter.exportProperty(CALayer::class.java, "cornerRadius")
    exporter.exportProperty(CALayer::class.java, "borderWidth")
    exporter.exportProperty(CALayer::class.java, "borderColor")
}