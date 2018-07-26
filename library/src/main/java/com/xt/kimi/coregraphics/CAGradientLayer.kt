package com.xt.kimi.coregraphics

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import com.xt.endo.CGPoint
import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.scale

class CAGradientLayer: CALayer() {

    var colors: List<UIColor> = listOf()
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var locations: List<Double> = listOf()
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var startPoint: CGPoint = CGPoint(0.0, 0.0)
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    var endPoint: CGPoint = CGPoint(1.0, 0.0)
        set(value) {
            field = value
            this.view?.setNeedsDisplay()
        }

    override fun drawContent(ctx: Canvas) {
        super.drawContent(ctx)
        sharedContentPaint.reset()
        sharedContentPaint.shader = LinearGradient(
                (this.frame.width * this.startPoint.x * scale).toFloat(),
                (this.frame.height * this.startPoint.y * scale).toFloat(),
                (this.frame.width * this.endPoint.x * scale).toFloat(),
                (this.frame.height * this.endPoint.y * scale).toFloat(),
                this.colors.map { return@map it.toInt() }.toIntArray(),
                this.locations.map { return@map it.toFloat() }.toFloatArray(),
                Shader.TileMode.CLAMP
        )
        ctx.drawPath(this.createBoundsPath(), sharedContentPaint)
    }

}

fun KIMIPackage.installCAGradientLayer() {
    exporter.exportClass(CAGradientLayer::class.java, "CAGradientLayer", "CALayer")
    exporter.exportProperty(CAGradientLayer::class.java, "colors")
    exporter.exportProperty(CAGradientLayer::class.java, "locations")
    exporter.exportProperty(CAGradientLayer::class.java, "startPoint")
    exporter.exportProperty(CAGradientLayer::class.java, "endPoint")
}