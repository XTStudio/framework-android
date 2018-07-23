package com.xt.kimi.uikit

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Region
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CALayer

/**
 * Created by cuiminghui on 2018/7/20.
 */

var scale: Float = 1.0f

enum class UIViewContentMode {
    scaleToFill,
    scaleAspectFit,
    scaleAspectFill
}

open class UIView : FrameLayout(EDOExporter.sharedExporter.applicationContext) {

    fun attachToActivity(activity: Activity) {
        UIView.findScale(activity)
        (this.parent as? ViewGroup)?.removeView(this)
        val rootView = UIWindow()
        rootView.addView(this, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        activity.setContentView(rootView)
    }

    init {
        clipChildren = false
        clipToPadding = false
        setWillNotDraw(false)
    }

    val layer = CALayer()

    // Geometry

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.layer.frame = frame
            this.bounds = CGRect(0.0, 0.0, frame.width, frame.height)
            this.setNeedsLayout()
        }

    var bounds: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        private set

    var center: CGPoint
        get() {
            return CGPoint(this.frame.x + this.frame.width / 2.0, this.frame.y + this.frame.height / 2.0)
        }
        set(value) {
            this.frame = CGRect(value.x - this.frame.width / 2.0, value.y - this.frame.height, this.frame.width, this.frame.height)
        }

    var transform: CGAffineTransform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

    // Hierarchy

    var tag: Int = 0

    var superview: UIView? = null
        private set

    internal val window: UIWindow?
        get() {
            (this as? UIWindow)?.let { return this }
            var current: UIView? = superview
            while (current != null) {
                (current as? UIWindow)?.let { return it }
                current = current.superview
            }
            return null
        }

    var subviews: List<UIView> = listOf()
        private set

    fun removeFromSuperview() {
        superview?.let { superview ->
            superview.willRemoveSubview(this)
            this.willMoveToSuperview(null)
            superview.subviews = kotlin.run {
                val subviews = superview.subviews.toMutableList()
                subviews.remove(this)
                return@run subviews.toList()
            }
            superview.removeView(this)
            superview.setNeedsDisplay()
            this.superview = null
            this.didMoveToSuperview()
        }
    }

    fun insertSubviewAtIndex(view: UIView, index: Int) {
        if (this == view) { return }
        if (view.superview != null) {
            view.removeFromSuperview()
        }
        view.willMoveToSuperview(this)
        view.superview = this
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(index, view)
            return@run subviews.toList()
        }
        this.addView(view, index, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
        view.didMoveToSuperview()
        this.didAddSubview(view)
    }

    fun exchangeSubview(index1: Int, index2: Int) {
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            val view1 = subviews[index1]
            val view2 = subviews[index2]
            if (index1 > index2) {
                subviews.removeAt(index1)
                subviews.removeAt(index2)
                subviews.add(index2, view1)
                subviews.add(index1, view2)
                this.removeView(view1)
                this.removeView(view2)
                this.addView(view1, index2, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                this.addView(view2, index1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
            else if (index1 < index2) {
                subviews.removeAt(index2)
                subviews.removeAt(index1)
                subviews.add(index1, view2)
                subviews.add(index2, view1)
                this.removeView(view2)
                this.removeView(view1)
                this.addView(view2, index1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                this.addView(view1, index2, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
            return@run subviews.toList()
        }
        this.setNeedsDisplay()
    }

    fun addSubview(view: UIView) {
        if (this == view) { return }
        if (view.superview != null) {
            view.removeFromSuperview()
        }
        view.willMoveToSuperview(this)
        view.superview = this
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(view)
            return@run subviews.toList()
        }
        this.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
        view.didMoveToSuperview()
        this.didAddSubview(view)
    }

    fun insertSubviewBelowSubview(view: UIView, belowSubview: UIView) {
        if (this == view) { return }
        subviews.indexOf(belowSubview)?.let { targetIndex ->
            if (view.superview != null) {
                view.removeFromSuperview()
            }
            view.willMoveToSuperview(this)
            view.superview = this
            subviews = kotlin.run {
                val subviews = this.subviews.toMutableList()
                subviews.add(targetIndex, view)
                return@run subviews.toList()
            }
            this.addView(view, targetIndex, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            this.setNeedsDisplay()
            view.didMoveToSuperview()
            this.didAddSubview(view)
        }
    }

    fun insertSubviewAboveSubview(view: UIView, belowSubview: UIView) {
        if (this == view) { return }
        subviews.indexOf(belowSubview)?.let { targetIndex ->
            if (view.superview != null) {
                view.removeFromSuperview()
            }
            view.willMoveToSuperview(this)
            view.superview = this
            subviews = kotlin.run {
                val subviews = this.subviews.toMutableList()
                subviews.add(targetIndex + 1, view)
                return@run subviews.toList()
            }
            this.addView(view, targetIndex + 1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            this.setNeedsDisplay()
            view.didMoveToSuperview()
            this.didAddSubview(view)
        }
    }

    fun bringSubviewToFront(view: UIView) {
        if (subviews.count() <= 1) { return }
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(subviews.removeAt(0))
            return@run subviews.toList()
        }
        this.removeView(view)
        this.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
    }

    fun sendSubviewToBack(view: UIView) {
        if (subviews.count() <= 1) { return }
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(0, subviews.removeAt(subviews.count() - 1))
            return@run subviews.toList()
        }
        this.removeView(view)
        this.addView(view, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
    }

    fun isDescendantOfView(view: UIView): Boolean {
        if (this == view) { return true }
        var current = this.superview
        while (current != null) {
            if (current == view) {
                return true
            }
            current = current.superview
        }
        return false
    }

    fun viewWithTag(tag: Int): UIView? {
        subviews.firstOrNull{ it.tag == tag }?.let { return it }
        subviews.forEach { it.viewWithTag(tag)?.let { return it } }
        return null
    }

    fun didAddSubview(subview: UIView) {
        EDOJavaHelper.invokeBindedMethod(this, "didAddSubview", subview)
    }

    fun willRemoveSubview(subview: UIView) {
        EDOJavaHelper.invokeBindedMethod(this, "willRemoveSubview", subview)
    }

    fun willMoveToSuperview(newSuperview: UIView?) {
        EDOJavaHelper.invokeBindedMethod(this, "willMoveToSuperview", newSuperview)
    }

    fun didMoveToSuperview() {
        EDOJavaHelper.invokeBindedMethod(this, "didMoveToSuperview")
    }

    fun setNeedsLayout() {
        this.requestLayout()
        this.layoutSubviews()
    }

    fun layoutIfNeeded() {
        this.requestLayout()
        this.layoutSubviews()
    }

    fun layoutSubviews() {
        EDOJavaHelper.invokeBindedMethod(this, "layoutSubviews")
    }

    // Rendering

    fun setNeedsDisplay() {
        this.invalidate()
    }

    var clipsToBounds: Boolean = false
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

    var backgroundColor: UIColor? = null
        set(value) {
            field = value
            this.layer.backgroundColor = value
            this.setNeedsDisplay()
        }

    var edo_alpha: Double
        get() {
            return this.alpha.toDouble()
        }
        set(value) {
            this.alpha = value.toFloat()
        }

    var hidden: Boolean = false
        set(value) {
            field = value
            this.visibility = if (value) View.GONE else View.VISIBLE
            this.setNeedsDisplay()
        }

    var contentMode: UIViewContentMode = UIViewContentMode.scaleToFill

    var tintColor: UIColor? = null
        set(value) {
            field = value
            this.tintColorDidChange()
        }

    fun tintColorDidChange() {
        EDOJavaHelper.invokeBindedMethod(this, "tintColorDidChange")
        subviews.forEach {
            if (it.tintColor == null) {
                it.tintColorDidChange()
            }
        }
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
        val canvas = canvas ?: return
        canvas.save()
        if (!this.transform.isIdentity()) {
            val unmatrix = this.transform.unmatrix()
            val matrix = Matrix()
            matrix.postTranslate(-(this.width / 2.0).toFloat(), -(this.height / 2.0).toFloat())
            matrix.postRotate(unmatrix.degree.toFloat())
            matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
            matrix.postTranslate((unmatrix.translate.x * scale).toFloat(), (unmatrix.translate.y * scale).toFloat())
            matrix.postTranslate((this.width / 2.0).toFloat(), (this.height / 2.0).toFloat())
            canvas.concat(matrix)
        }
        canvas.let {
            this.layer.drawInContext(it)
        }
        if (!this.clipsToBounds) {
            val rect = canvas.clipBounds
            rect.inset(-100000, -100000)
            canvas.clipRect(rect, Region.Op.REPLACE)
        }
        else {

        }
        super.draw(canvas)
        canvas.restore()
    }

    open fun hitTest(point: CGPoint): UIView? {
        if (this.userInteractionEnabled && this.alpha > 0.0 && !this.hidden && this.pointInside(point)) {
            this.subviews.forEach {
                val convertedPoint = it.convertPointFromView(point, this)
                it.hitTest(convertedPoint)?.let { return it }
            }
            return this
        }
        return null
    }

    open fun pointInside(point: CGPoint): Boolean {
        return point.x >= 0.0 && point.y >= 0.0 && point.x <= this.frame.width && point.y <= this.frame.height
    }

    fun convertPointFromView(point: CGPoint, fromView: UIView): CGPoint {
        val fromPoint = fromView.convertPointToWindow(point) ?: return point
        return this.convertPointFromWindow(fromPoint) ?: return point
    }

    fun convertPointToWindow(point: CGPoint): CGPoint? {
        if (this.window == null) {
            return null
        }
        var matrix = Matrix()
        var current: UIView? = this
        var routes: MutableList<UIView> = mutableListOf()
        while (current != null) {
            if (current is UIWindow) { break }
            routes.add(0, current)
            current = current.superview
        }
        routes.forEach {
            matrix.postTranslate(it.frame.x.toFloat(), it.frame.y.toFloat())
            if (!it.transform.isIdentity()) {
                matrix.postConcat(it.transform.toNativeMatrix())
            }
        }
        var fromArr = FloatArray(9)
        matrix.getValues(fromArr)
        return CGPoint(point.x * fromArr[0] + point.x * fromArr[3] + fromArr[2], point.y * fromArr[1] + point.y * fromArr[4] + fromArr[5])
    }

    fun convertPointFromWindow(point: CGPoint): CGPoint? {
        if (this.window == null) {
            return null
        }
        var matrix = Matrix()
        var current: UIView? = this
        var routes: MutableList<UIView> = mutableListOf()
        while (current != null) {
            if (current is UIWindow) { break }
            routes.add(0, current)
            current = current.superview
        }
        routes.forEach {
            matrix.postTranslate(it.frame.x.toFloat(), it.frame.y.toFloat())
            if (!it.transform.isIdentity()) {
                matrix.postConcat(it.transform.toNativeMatrix())
            }
        }
        var fromArr = FloatArray(9)
        matrix.getValues(fromArr)
        return CGPoint(
                (point.x - fromArr[2]) / (fromArr[0] + fromArr[3]),
                (point.y - fromArr[5]) / (fromArr[1] + fromArr[4])
        )
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
    exporter.exportProperty(UIView::class.java, "bounds")
    exporter.exportProperty(UIView::class.java, "center")
    exporter.exportProperty(UIView::class.java, "transform")
    exporter.exportProperty(UIView::class.java, "tag")
    exporter.exportProperty(UIView::class.java, "superview", true)
    exporter.exportProperty(UIView::class.java, "subviews", true)
    exporter.exportMethodToJavaScript(UIView::class.java, "removeFromSuperview")
    exporter.exportMethodToJavaScript(UIView::class.java, "insertSubviewAtIndex")
    exporter.exportMethodToJavaScript(UIView::class.java, "exchangeSubview")
    exporter.exportMethodToJavaScript(UIView::class.java, "addSubview")
    exporter.exportMethodToJavaScript(UIView::class.java, "insertSubviewBelowSubview")
    exporter.exportMethodToJavaScript(UIView::class.java, "insertSubviewAboveSubview")
    exporter.exportMethodToJavaScript(UIView::class.java, "bringSubviewToFront")
    exporter.exportMethodToJavaScript(UIView::class.java, "sendSubviewToBack")
    exporter.exportMethodToJavaScript(UIView::class.java, "isDescendantOfView")
    exporter.exportMethodToJavaScript(UIView::class.java, "viewWithTag")
    exporter.bindMethodToJavaScript(UIView::class.java, "didAddSubview")
    exporter.bindMethodToJavaScript(UIView::class.java, "willRemoveSubview")
    exporter.bindMethodToJavaScript(UIView::class.java, "willMoveToSuperview")
    exporter.bindMethodToJavaScript(UIView::class.java, "didMoveToSuperview")
    exporter.exportMethodToJavaScript(UIView::class.java, "setNeedsLayout")
    exporter.exportMethodToJavaScript(UIView::class.java, "layoutIfNeeded")
    exporter.bindMethodToJavaScript(UIView::class.java, "layoutSubviews")
    exporter.exportMethodToJavaScript(UIView::class.java, "setNeedsDisplay")
    exporter.exportProperty(UIView::class.java, "clipsToBounds")
    exporter.exportProperty(UIView::class.java, "backgroundColor")
    exporter.exportProperty(UIView::class.java, "edo_alpha")
    exporter.exportProperty(UIView::class.java, "hidden")
    exporter.exportProperty(UIView::class.java, "contentMode")
    exporter.exportProperty(UIView::class.java, "tintColor")
    exporter.bindMethodToJavaScript(UIView::class.java, "tintColorDidChange")
    exporter.exportEnum("UIViewContentMode", mapOf(
            Pair("scaleToFill", UIViewContentMode.scaleToFill),
            Pair("scaleAspectFit", UIViewContentMode.scaleAspectFit),
            Pair("scaleAspectFill", UIViewContentMode.scaleAspectFill)
    ))
    exporter.exportProperty(UIView::class.java, "userInteractionEnabled")
    exporter.exportProperty(UIView::class.java, "gestureRecognizers", true)
    exporter.exportMethodToJavaScript(UIView::class.java, "addGestureRecognizer")
    exporter.exportMethodToJavaScript(UIView::class.java, "removeGestureRecognizer")
}

fun CGAffineTransform.isIdentity(): Boolean {
    return this.a == 1.0 && this.b == 0.0 && this.c == 0.0 && this.d == 1.0 && this.tx == 0.0 && this.ty == 0.0
}

class CGMatrix(val scale: CGPoint, val degree: Double, val translate: CGPoint)

fun CGAffineTransform.unmatrix(): CGMatrix {
    var A = a
    var B = b
    var C = c
    var D = d
    if (A * D == B * C) {
        return CGMatrix(CGPoint(1.0, 1.0), 0.0, CGPoint(0.0, 0.0))
    }
    var scaleX = Math.sqrt(A * A + B * B)
    A /= scaleX
    B /= scaleX
    var skew = A * C + B * D
    C -= A * skew
    D -= B * skew
    var scaleY = Math.sqrt(C * C + D * D)
    C /= scaleY
    D /= scaleY
    skew /= scaleY
    if ( A * D < B * C ) {
        A = -A
        B = -B
        skew = -skew
        scaleX = -scaleX
    }
    return CGMatrix(CGPoint(scaleX, scaleY), Math.atan2(B, A) / (Math.PI / 180), CGPoint(tx, ty))
}

fun CGAffineTransform.toNativeMatrix(): Matrix {
    val matrix = Matrix()
    val arr = FloatArray(9)
    arr[0] = a.toFloat()
    arr[1] = c.toFloat()
    arr[2] = tx.toFloat()
    arr[3] = b.toFloat()
    arr[4] = d.toFloat()
    arr[5] = ty.toFloat()
    arr[6] = 0.0.toFloat()
    arr[7] = 0.0.toFloat()
    arr[8] = 1.0.toFloat()
    matrix.setValues(arr)
    return matrix
}