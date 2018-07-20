package com.xt.kimi.uikit

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xt.endo.CGAffineTransform
import com.xt.endo.CGPoint
import com.xt.endo.CGRect
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CALayer
import kotlin.math.exp

/**
 * Created by cuiminghui on 2018/7/20.
 */

var scale: Float = 1.0f

open class UIView : FrameLayout(EDOExporter.sharedExporter.applicationContext) {

//    open val nativeView: FrameLayout = object: FrameLayout(EDOExporter.sharedExporter.applicationContext) {
//
//        init {
//            setWillNotDraw(false)
//        }
//
//        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//            frame?.let {
//                setMeasuredDimension((it.width * scale).toInt(), (it.height * scale).toInt())
//            }
//        }
//
//        override fun draw(canvas: Canvas?) {
//            canvas?.let { canvas ->
//                canvas.save()
//                canvas.translate(frame.x.toFloat() * scale, frame.y.toFloat() * scale)
//                this@UIView.layer.drawInContext(canvas)
//                super.draw(canvas)
//                canvas.restore()
//            }
//        }
//
//    }

    fun attachToActivity(activity: Activity) {
        UIView.findScale(activity)
        activity.setContentView(this)
    }

    init {
        setWillNotDraw(false)
    }

    val layer = CALayer()

    // Geometry

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.layer.frame = frame
            this.setNeedsLayout()
        }

    var bounds: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        private set

    var center: CGPoint = CGPoint(0.0, 0.0)

    var transform: CGAffineTransform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

    // Hierarchy

    var tag: Int = 0

    var superview: UIView? = null
        private set

    var subviews: List<UIView> = listOf()
        private set

    fun removeFromSuperview() {
        superview?.let { superview ->
            superview.subviews = kotlin.run {
                val subviews = superview.subviews.toMutableList()
                subviews.remove(this)
                return@run subviews.toList()
            }
            superview.setNeedsDisplay()
            this.superview = null
        }
    }

    fun insertSubviewAtIndex(view: UIView, index: Int) {

    }

    fun exchangeSubview(index1: Int, index2: Int) {

    }

    fun addSubview(view: UIView) {
        if (view.superview != null) {
            view.removeFromSuperview()
        }
        view.superview = this
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(view)
            return@run subviews.toList()
        }
        this.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
    }

    fun insertSubviewBelowSubview(view: UIView, belowSubview: UIView) {

    }

    fun insertSubviewAboveSubview(view: UIView, belowSubview: UIView) {

    }

    fun bringSubviewToFront(view: UIView) {

    }

    fun sendSubviewToBack(view: UIView) {

    }

    fun isDescendantOfView(view: UIView) {

    }

    fun viewWithTag(tag: Int): UIView? {
        return null
    }

    fun didAddSubview(subview: UIView) {

    }

    fun willRemoveSubview(subview: UIView) {

    }

    fun willMoveToSuperview(newSuperview: UIView) {

    }

    fun didMoveToSuperview() {

    }

    fun setNeedsLayout() {
        this.requestLayout()
    }

    fun layoutIfNeeded() {

    }

    fun layoutSubviews() {

    }

    // Rendering

    fun setNeedsDisplay() {
        this.invalidate()
    }

    var clipsToBounds: Boolean = false

    var backgroundColor: UIColor? = null
        set(value) {
            field = value
            this.layer.backgroundColor = value
        }

    var edo_alpha: Double
        get() {
            return this.alpha.toDouble()
        }
        set(value) {
            this.alpha = value.toFloat()
        }

    var hidden: Boolean = false

    var contentMode: Int = 0

    var tintColor: Int = 0

    fun tintColorDidChange() {

    }

    // GestureRecognizers

    var userInteractionEnabled: Boolean = true

    var gestureRecognizers: List<Any> = listOf()
        private set

    fun addGestureRecognizer(gestureRecognizer: Any) {}

    fun removeGestureRecognizer(gestureRecognizer: Any) {}

    // Accessibility

    var isAccessibilityElement: Boolean = false

    var accessibilityLabel: String? = null

    var accessibilityHint: String? = null

    var accessibilityValue: String? = null

    var accessibilityIdentifier: String? = null


    //

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        frame?.let {
            val scale = resources.displayMetrics.density
            x = (it.x * scale).toFloat()
            y = (it.y * scale).toFloat()
            setMeasuredDimension((it.width * scale).toInt(), (it.height * scale).toInt())
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.let {
            this.layer.drawInContext(it)
        }
        super.draw(canvas)
    }

    companion object {

        private fun findScale(activity: Activity) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            scale = metrics.density
        }

    }

}

fun KIMIPackage.installUIView() {
    exporter.exportClass(UIView::class.java, "UIView")
    exporter.exportProperty(UIView::class.java, "frame")
    exporter.exportProperty(UIView::class.java, "backgroundColor")
    exporter.exportProperty(UIView::class.java, "edo_alpha")
    exporter.exportMethodToJavaScript(UIView::class.java, "removeFromSuperview")
    exporter.exportMethodToJavaScript(UIView::class.java, "addSubview")
}