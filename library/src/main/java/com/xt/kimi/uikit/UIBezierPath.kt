package com.xt.kimi.uikit

import android.graphics.Path
import com.xt.endo.CGPoint
import com.xt.kimi.KIMIPackage

class UIBezierPath: Path() {

    fun edo_moveTo(toPoint: CGPoint) {
        this.moveTo((toPoint.x * scale).toFloat(), (toPoint.y * scale).toFloat())
    }

    fun edo_addLineTo(toPoint: CGPoint) {
        this.lineTo((toPoint.x * scale).toFloat(), (toPoint.y * scale).toFloat())
    }

    fun edo_addArcTo(toCenter: CGPoint, radius: Double, startAngle: Double, endAngle: Double, closewise: Boolean) {
        
    }

    fun edo_addCurveTo(toPoint: CGPoint, controlPoint1: CGPoint, controlPoint2: CGPoint) {

    }

    fun edo_addQuadCurveTo(toPoint: CGPoint, controlPoint: CGPoint) {

    }

    fun edo_closePath() {

    }

    fun edo_removeAllPoints() {

    }

    fun edo_appendPath(path: UIBezierPath) {

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