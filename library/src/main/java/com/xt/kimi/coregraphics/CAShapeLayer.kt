package com.xt.kimi.coregraphics

import com.xt.kimi.KIMIPackage
import com.xt.kimi.uikit.UIBezierPath
import com.xt.kimi.uikit.UIColor

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

    /**
     * The color used to fill the shape’s path.
     */
    var fillColor: UIColor? = UIColor.black

    /**
     * The fill rule used when filling the shape’s path.
     */
    var fillRule: CAShapeFillRule = CAShapeFillRule.evenOdd

    /**
     * Specifies the line cap style for the shape’s path.
     */
    var lineCap: CAShapeLineCap = CAShapeLineCap.butt

    /**
     * The dash pattern applied to the shape’s path when stroked.
     */
    var lineDashPattern: List<Double> = listOf()

    /**
     * The dash phase applied to the shape’s path when stroked.
     */
    var lineDashPhase: Double = 0.0

    /**
     * Specifies the line join style for the shape’s path.
     */
    var lineJoin: CAShapeLineJoin = CAShapeLineJoin.miter

    /**
     * Specifies the line width of the shape’s path.
     */
    var lineWidth: Double = 1.0

    /**
     * The miter limit used when stroking the shape’s path.
     */
    var miterLimit: Double = 10.0

    /**
     * The color used to stroke the shape’s path.
     */
    var strokeColor: UIColor? = null

    /**
     * The relative location at which to begin stroking the path.
     */
    var strokeStart: Double = 0.0

    /**
     * The relative location at which to stop stroking the path.
     */
    var strokeEnd: Double = 1.0

}

fun KIMIPackage.installCAShapeLayer() {
    exporter.exportClass(CAShapeLayer::class.java, "CAShapeLayer", "CALayer")
    exporter.exportProperty(CAShapeLayer::class.java, "path")
    exporter.exportProperty(CAShapeLayer::class.java, "fillColor")
    exporter.exportProperty(CAShapeLayer::class.java, "fillRule")
    exporter.exportProperty(CAShapeLayer::class.java, "lineCap")
    exporter.exportProperty(CAShapeLayer::class.java, "lineDashPattern")
    exporter.exportProperty(CAShapeLayer::class.java, "lineDashPhase")
    exporter.exportProperty(CAShapeLayer::class.java, "lineJoin")
    exporter.exportProperty(CAShapeLayer::class.java, "lineWidth")
    exporter.exportProperty(CAShapeLayer::class.java, "miterLimit")
    exporter.exportProperty(CAShapeLayer::class.java, "strokeColor")
    exporter.exportProperty(CAShapeLayer::class.java, "strokeStart")
    exporter.exportProperty(CAShapeLayer::class.java, "strokeEnd")
}