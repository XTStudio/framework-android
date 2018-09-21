package com.xt.kimi.coregraphics

import android.graphics.*
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIBezierPath
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.scale

enum class CAShapeFillRule {
    nonZero,
    evenOdd
}

enum class CAShapeLineCap {
    butt,
    round,
    square
}

enum class CAShapeLineJoin {
    miter,
    round,
    bevel
}

class CAShapeLayer: CALayer() {

    var path: UIBezierPath? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "path")
            this.view?.setNeedsDisplay()
        }

    var fillColor: UIColor? = UIColor.black
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "fillColor")
            this.view?.setNeedsDisplay()
        }

    var fillRule: CAShapeFillRule = CAShapeFillRule.evenOdd
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "fillRule")
            this.view?.setNeedsDisplay()
        }

    var lineCap: CAShapeLineCap = CAShapeLineCap.butt
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineCap")
            this.view?.setNeedsDisplay()
        }

    var lineDashPattern: List<Double> = listOf()
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineDashPattern")
            this.view?.setNeedsDisplay()
        }

    var lineDashPhase: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineDashPhase")
            this.view?.setNeedsDisplay()
        }

    var lineJoin: CAShapeLineJoin = CAShapeLineJoin.miter
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineJoin")
            this.view?.setNeedsDisplay()
        }

    var lineWidth: Double = 1.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "lineWidth")
            this.view?.setNeedsDisplay()
        }

    var miterLimit: Double = 10.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "miterLimit")
            this.view?.setNeedsDisplay()
        }

    var strokeColor: UIColor? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "strokeColor")
            this.view?.setNeedsDisplay()
        }

    var strokeStart: Double = 0.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "strokeStart")
            this.view?.setNeedsDisplay()
        }

    var strokeEnd: Double = 1.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "strokeEnd")
            this.view?.setNeedsDisplay()
        }

    override fun drawContent(ctx: Canvas) {
        super.drawContent(ctx)
        this.path?.let { path ->
            fillColor?.let {
                CALayer.sharedContentPaint.reset()
                CALayer.sharedContentPaint.isAntiAlias = true
                CALayer.sharedContentPaint.color = it.toInt()
                CALayer.sharedContentPaint.style = Paint.Style.FILL
                path.fillType = kotlin.run {
                    return@run when (this.fillRule) {
                        CAShapeFillRule.nonZero -> Path.FillType.WINDING
                        CAShapeFillRule.evenOdd -> Path.FillType.EVEN_ODD
                    }
                }
                this.setAlphaForPaint(CALayer.sharedContentPaint, this)
                ctx.drawPath(path, CALayer.sharedContentPaint)
            }
            strokeColor?.takeIf { this.lineWidth > 0 }?.let {
                val strokePath = CAPathUtils.trimPathIfNeeded(path, this.strokeStart.toFloat(), this.strokeEnd.toFloat(), 0.0f)
                CALayer.sharedContentPaint.reset()
                CALayer.sharedContentPaint.isAntiAlias = true
                CALayer.sharedContentPaint.color = it.toInt()
                CALayer.sharedContentPaint.strokeCap = kotlin.run {
                    return@run when(this.lineCap) {
                        CAShapeLineCap.butt -> Paint.Cap.BUTT
                        CAShapeLineCap.round -> Paint.Cap.ROUND
                        CAShapeLineCap.square -> Paint.Cap.SQUARE
                    }
                }
                this.lineDashPattern.takeIf { it.count() > 0 }?.let {
                    CALayer.sharedContentPaint.pathEffect = DashPathEffect(it.map { return@map it.toFloat() }.toFloatArray(), (this.lineDashPhase * scale).toFloat())
                }
                CALayer.sharedContentPaint.strokeJoin = kotlin.run {
                    return@run when(this.lineJoin) {
                        CAShapeLineJoin.miter -> Paint.Join.MITER
                        CAShapeLineJoin.bevel -> Paint.Join.BEVEL
                        CAShapeLineJoin.round -> Paint.Join.ROUND
                    }
                }
                CALayer.sharedContentPaint.strokeWidth = (this.lineWidth * scale).toFloat()
                CALayer.sharedContentPaint.strokeMiter = (this.miterLimit * scale).toFloat()
                CALayer.sharedContentPaint.style = Paint.Style.STROKE
                this.setAlphaForPaint(CALayer.sharedContentPaint, this)
                ctx.drawPath(strokePath, CALayer.sharedContentPaint)
            }
        }
    }

}

private class CAPathUtils {

    companion object {

        private val pathMeasure = PathMeasure()
        private val tempPath = Path()
        private val tempPath2 = Path()

        fun trimPathIfNeeded(path: Path, startValue: Float, endValue: Float, offsetValue: Float): Path {
            pathMeasure.setPath(path, false)
            val length = pathMeasure.length
            if (startValue == 1f && endValue == 0f) {
                return path
            }
            if (length < 1f || Math.abs(endValue - startValue - 1f) < .01) {
                return path
            }
            val start = length * startValue
            val end = length * endValue
            var newStart = Math.min(start, end)
            var newEnd = Math.max(start, end)
            val offset = offsetValue * length
            newStart += offset
            newEnd += offset
            if (newStart >= length && newEnd >= length) {
                newStart = this.floorMod(newStart, length)
                newEnd = this.floorMod(newEnd, length)
            }

            if (newStart < 0) {
                newStart = this.floorMod(newStart, length)
            }
            if (newEnd < 0) {
                newEnd = this.floorMod(newEnd, length)
            }
            if (newStart == newEnd) {
                tempPath.reset()
                return tempPath
            }
            if (newStart >= newEnd) {
                newStart -= length
            }
            tempPath.reset()
            pathMeasure.getSegment(
                    newStart,
                    newEnd,
                    tempPath,
                    true)

            if (newEnd > length) {
                tempPath2.reset()
                pathMeasure.getSegment(
                        0f,
                        newEnd % length,
                        tempPath2,
                        true)
                tempPath.addPath(tempPath2)
            } else if (newStart < 0) {
                tempPath2.reset()
                pathMeasure.getSegment(
                        length + newStart,
                        length,
                        tempPath2,
                        true)
                tempPath.addPath(tempPath2)
            }
            return tempPath
        }

        private fun floorMod(x: Float, y: Float): Float {
            return floorMod(x.toInt(), y.toInt()).toFloat()
        }

        private fun floorMod(x: Int, y: Int): Int {
            return x - y * floorDiv(x, y)
        }

        private fun floorDiv(x: Int, y: Int): Int {
            var r = x / y
            val sameSign = x xor y >= 0
            val mod = x % y
            if (!sameSign && mod != 0) {
                r--
            }
            return r
        }

    }

}

fun KIMIPackage.installCAShapeLayer() {
    exporter.exportClass(CAShapeLayer::class.java, "CAShapeLayer", "CALayer")
    exporter.exportProperty(CAShapeLayer::class.java, "path", false, true)
    exporter.exportProperty(CAShapeLayer::class.java, "fillColor", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "fillRule", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "lineCap", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "lineDashPattern", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "lineDashPhase", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "lineJoin", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "lineWidth", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "miterLimit", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "strokeColor", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "strokeStart", false, true, true)
    exporter.exportProperty(CAShapeLayer::class.java, "strokeEnd", false, true, true)
}