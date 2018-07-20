package com.xt.kimi.coregraphics

import android.graphics.*
import com.xt.endo.CGRect
import com.xt.kimi.uikit.UIColor
import com.xt.kimi.uikit.scale

/**
 * Created by cuiminghui on 2018/7/20.
 */

private val sharedOuterPath = Path()
private val sharedBackgroundPaint = Paint()

open class CALayer {

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)

    var backgroundColor: UIColor? = null

    open fun drawInContext(ctx: Canvas) {
        sharedOuterPath.reset()
        sharedOuterPath.addRect(RectF(0.0f, 0.0f, this.frame.width.toFloat() * scale, this.frame.height.toFloat() * scale), Path.Direction.CCW)
        sharedBackgroundPaint.reset()
        sharedBackgroundPaint.color = Color.TRANSPARENT
        backgroundColor?.let {
            sharedBackgroundPaint.color = Color.argb(Math.ceil(it.a * 255.0).toInt(), Math.ceil(it.r * 255.0).toInt(), Math.ceil(it.g * 255.0).toInt(), Math.ceil(it.b * 255.0).toInt())
        }
        ctx.drawPath(sharedOuterPath, sharedBackgroundPaint)
    }

}