package com.xt.kimi.coregraphics

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import com.xt.endo.CGPoint
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.scale

class CAGradientLayer: CALayer() {

    var colors: List<UIColor> = listOf()
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "colors")
            this.view?.setNeedsDisplay()
        }

    var locations: List<Double> = listOf()
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "locations")
            this.view?.setNeedsDisplay()
        }

    var startPoint: CGPoint = CGPoint(0.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "startPoint")
            this.view?.setNeedsDisplay()
        }

    var endPoint: CGPoint = CGPoint(1.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "endPoint")
            this.view?.setNeedsDisplay()
        }

    override fun drawContent(ctx: Canvas) {
        super.drawContent(ctx)
        var locations: MutableList<Double> = mutableListOf()
        if (locations.count() != colors.count()) {
            var i = 0.0
            (0 until colors.count()).forEach {
                locations.add(i)
                i += 1.0 / (colors.count() - 1)
            }
        }
        else {
            locations = this.locations.toMutableList()
        }
        CALayer.sharedContentPaint.reset()
        CALayer.sharedContentPaint.shader = LinearGradient(
                (this.frame.width * this.startPoint.x * scale).toFloat(),
                (this.frame.height * this.startPoint.y * scale).toFloat(),
                (this.frame.width * this.endPoint.x * scale).toFloat(),
                (this.frame.height * this.endPoint.y * scale).toFloat(),
                this.colors.map { return@map it.toInt() }.toIntArray(),
                locations.map { return@map it.toFloat() }.toFloatArray(),
                Shader.TileMode.CLAMP
        )
        this.setAlphaForPaint(CALayer.sharedContentPaint, this)
        ctx.drawPath(this.createBoundsPath(), CALayer.sharedContentPaint)
    }

}

fun KIMIPackage.installCAGradientLayer() {
    exporter.exportClass(CAGradientLayer::class.java, "CAGradientLayer", "CALayer")
    exporter.exportProperty(CAGradientLayer::class.java, "colors", false, true, true)
    exporter.exportProperty(CAGradientLayer::class.java, "locations", false, true, true)
    exporter.exportProperty(CAGradientLayer::class.java, "startPoint", false, true, true)
    exporter.exportProperty(CAGradientLayer::class.java, "endPoint", false, true, true)
}