package com.xt.kimi.uikit

import android.graphics.Path
import com.xt.endo.CGPoint
import com.xt.kimi.KIMIPackage

class UIBezierPath: Path() {

    val scale = com.xt.kimi.uikit.scale

    fun edo_moveTo(toPoint: CGPoint) {
        this.moveTo((toPoint.x * scale).toFloat(), (toPoint.y * scale).toFloat())
    }

    fun edo_addLineTo(toPoint: CGPoint) {
        this.lineTo((toPoint.x * scale).toFloat(), (toPoint.y * scale).toFloat())
    }

    fun edo_addArcTo(toCenter: CGPoint, radius: Double, startAngle: Double, endAngle: Double, closewise: Boolean) {
        this.arcTo(
                android.graphics.RectF(
                        (toCenter.x * scale - radius * scale).toFloat(),
                        (toCenter.y * scale - radius * scale).toFloat(),
                        (toCenter.x * scale + radius * scale).toFloat(),
                        (toCenter.y * scale + radius * scale).toFloat()
                ),
                startAngle.toFloat(),
                endAngle.toFloat(),
                closewise
        )
    }

    fun edo_addCurveTo(toPoint: CGPoint, controlPoint1: CGPoint, controlPoint2: CGPoint) {
        this.cubicTo(
                (controlPoint1.x * scale).toFloat(),
                (controlPoint1.y * scale).toFloat(),
                (controlPoint2.x * scale).toFloat(),
                (controlPoint2.y * scale).toFloat(),
                (toPoint.x * scale).toFloat(),
                (toPoint.y * scale).toFloat()
        )
    }

    fun edo_addQuadCurveTo(toPoint: CGPoint, controlPoint: CGPoint) {
        this.quadTo(
                (controlPoint.x * scale).toFloat(),
                (controlPoint.y * scale).toFloat(),
                (toPoint.x * scale).toFloat(),
                (toPoint.y * scale).toFloat()
        )
    }

    fun edo_closePath() {
        this.close()
    }

    fun edo_removeAllPoints() {
        this.reset()
    }

    fun edo_appendPath(path: UIBezierPath) {
        this.addPath(path)
    }

}


fun KIMIPackage.installUIBezierPath() {
    exporter.exportClass(UIBezierPath::class.java, "UIBezierPath")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_moveTo")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_addLineTo")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_addArcTo")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_addCurveTo")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_addQuadCurveTo")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_closePath")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_removeAllPoints")
    exporter.exportMethodToJavaScript(UIBezierPath::class.java, "edo_appendPath")
}