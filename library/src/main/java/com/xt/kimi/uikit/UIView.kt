package com.xt.kimi.uikit

import android.app.Activity
import android.graphics.*
import android.os.Build
import android.util.DisplayMetrics
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CALayer
import com.xt.kimi.coregraphics.CAOSCanvas
import java.lang.ref.SoftReference
import java.util.*
import kotlin.math.max
import kotlin.math.min

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
        this.removeFromSuperview()
        val rootView = UIWindow()
        rootView.setBackgroundColor(Color.WHITE)
        rootView.addSubview(this)
        activity.setContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    init {
        clipChildren = false
        clipToPadding = false
        setWillNotDraw(false)
    }

    val layer = CALayer()

    // Geometry

    private var edo_frame_animations: List<UIAnimation>? = null

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_frame_animations?.forEach { it.cancel() }
                this.edo_frame_animations = null
            }
            UIAnimator.activeAnimator?.let {
                it.animationCreater?.let {
                    val animations = mutableListOf<UIAnimation>()
                    if (field.x != value.x) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.frame = CGRect(it, this.frame.y, this.frame.width, this.frame.height)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.x)
                        animation.setEndValue(value.x)
                        animations.add(animation)
                    }
                    if (field.y != value.y) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.frame = CGRect(this.frame.x, it, this.frame.width, this.frame.height)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.y)
                        animation.setEndValue(value.y)
                        animations.add(animation)
                    }
                    if (field.width != value.width) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.frame = CGRect(this.frame.x, this.frame.y, it, this.frame.height)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.width)
                        animation.setEndValue(value.width)
                        animations.add(animation)
                    }
                    if (field.height != value.height) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.frame = CGRect(this.frame.x, this.frame.y, this.frame.width, it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.height)
                        animation.setEndValue(value.height)
                        animations.add(animation)
                    }
                    edo_frame_animations = animations.toList()
                    return
                }
            }
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

    private var edo_transform_animations: List<UIAnimation>? = null

    var transform: CGAffineTransform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_transform_animations?.forEach { it.cancel() }
                this.edo_transform_animations = null
            }
            UIAnimator.activeAnimator?.let {
                it.animationCreater?.let {
                    val animations = mutableListOf<UIAnimation>()
                    val fieldUnmatrix = field.unmatrix()
                    val valueUnmatrix = value.unmatrix()
                    if (fieldUnmatrix.scale.x != valueUnmatrix.scale.x) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.transform = this.transform.setScaleX(it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(fieldUnmatrix.scale.x)
                        animation.setEndValue(valueUnmatrix.scale.x)
                        animations.add(animation)
                    }
                    if (fieldUnmatrix.scale.y != valueUnmatrix.scale.y) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.transform = this.transform.setScaleY(it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(fieldUnmatrix.scale.y)
                        animation.setEndValue(valueUnmatrix.scale.y)
                        animations.add(animation)
                    }
                    if (fieldUnmatrix.degree != valueUnmatrix.degree) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.transform = this.transform.setDegree(it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(fieldUnmatrix.degree)
                        animation.setEndValue(valueUnmatrix.degree)
                        animations.add(animation)
                    }
                    if (fieldUnmatrix.translate.x != valueUnmatrix.translate.x) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.transform = this.transform.setTranslateX(it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(fieldUnmatrix.translate.x)
                        animation.setEndValue(valueUnmatrix.translate.x)
                        animations.add(animation)
                    }
                    if (fieldUnmatrix.translate.y != valueUnmatrix.translate.y) {
                        val animation = it()
                        animation.setUpdateListener {
                            UIAnimator.duringAnimationValueSet = true
                            this.transform = this.transform.setTranslateY(it)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(fieldUnmatrix.translate.y)
                        animation.setEndValue(valueUnmatrix.translate.y)
                        animations.add(animation)
                    }
                    edo_transform_animations = animations.toList()
                    return
                }
            }
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

    var clipsToBounds: Boolean
        get() {
            return this.layer.masksToBounds
        }
        set(value) {
            this.layer.masksToBounds = value
            this.setNeedsDisplay()
        }

    private var edo_backgroundColor_animations: List<UIAnimation>? = null

    var edo_backgroundColor: UIColor? = null
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_backgroundColor_animations?.forEach { it.cancel() }
                this.edo_backgroundColor_animations = null
            }
            UIAnimator.activeAnimator?.let {
                it.animationCreater?.let {
                    val field = field ?: return@let
                    val value = value ?: return@let
                    val animations = mutableListOf<UIAnimation>()
                    if (field.r != value.r) {
                        val animation = it()
                        animation.setUpdateListener {
                            val backgroundColor = this.edo_backgroundColor ?: return@setUpdateListener
                            UIAnimator.duringAnimationValueSet = true
                            this.edo_backgroundColor = UIColor(max(0.0, min(1.0, it)), backgroundColor.g, backgroundColor.b, backgroundColor.a)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.r)
                        animation.setEndValue(value.r)
                        animations.add(animation)
                    }
                    if (field.g != value.g) {
                        val animation = it()
                        animation.setUpdateListener {
                            val backgroundColor = this.edo_backgroundColor ?: return@setUpdateListener
                            UIAnimator.duringAnimationValueSet = true
                            this.edo_backgroundColor = UIColor(backgroundColor.r, max(0.0, min(1.0, it)), backgroundColor.b, backgroundColor.a)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.g)
                        animation.setEndValue(value.g)
                        animations.add(animation)
                    }
                    if (field.b != value.b) {
                        val animation = it()
                        animation.setUpdateListener {
                            val backgroundColor = this.edo_backgroundColor ?: return@setUpdateListener
                            UIAnimator.duringAnimationValueSet = true
                            this.edo_backgroundColor = UIColor(backgroundColor.r, backgroundColor.g, max(0.0, min(1.0, it)), backgroundColor.a)
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.b)
                        animation.setEndValue(value.b)
                        animations.add(animation)
                    }
                    if (field.a != value.a) {
                        val animation = it()
                        animation.setUpdateListener {
                            val backgroundColor = this.edo_backgroundColor ?: return@setUpdateListener
                            UIAnimator.duringAnimationValueSet = true
                            this.edo_backgroundColor = UIColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, max(0.0, min(1.0, it)))
                            UIAnimator.duringAnimationValueSet = false
                        }
                        animation.setStartValue(field.a)
                        animation.setEndValue(value.a)
                        animations.add(animation)
                    }
                    edo_backgroundColor_animations = animations.toList()
                    return
                }
            }
            field = value
            this.layer.backgroundColor = value
            this.setNeedsDisplay()
        }

    private var edo_alpha_animation: UIAnimation? = null

    var edo_alpha: Double
        get() {
            return this.alpha.toDouble()
        }
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_alpha_animation?.cancel()
                this.edo_alpha_animation = null
            }
            UIAnimator.activeAnimator?.let {
                it.animationCreater?.let {
                    val animation = it()
                    this.edo_alpha_animation = animation
                    animation.setUpdateListener {
                        UIAnimator.duringAnimationValueSet = true
                        this.alpha = it.toFloat()
                        UIAnimator.duringAnimationValueSet = false
                    }
                    animation.setStartValue(this.alpha.toDouble())
                    animation.setEndValue(value)
                    return
                }
            }
            this.alpha = value.toFloat()
        }

    var hidden: Boolean = false
        set(value) {
            field = value
            this.visibility = if (value) View.GONE else View.VISIBLE
            this.setNeedsDisplay()
        }

    var contentMode: UIViewContentMode = UIViewContentMode.scaleToFill
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

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

    var gestureRecognizers: List<UIGestureRecognizer> = listOf()
        private set

    fun addGestureRecognizer(gestureRecognizer: UIGestureRecognizer) {
        if (this.gestureRecognizers.contains(gestureRecognizer)) { return }
        this.gestureRecognizers = kotlin.run {
            val gestureRecognizers = this.gestureRecognizers.toMutableList()
            gestureRecognizers.add(gestureRecognizer)
            gestureRecognizer.view = this
            return@run gestureRecognizers.toList()
        }
    }

    fun removeGestureRecognizer(gestureRecognizer: UIGestureRecognizer) {
        if (!this.gestureRecognizers.contains(gestureRecognizer)) { return }
        this.gestureRecognizers = kotlin.run {
            val gestureRecognizers = this.gestureRecognizers.toMutableList()
            gestureRecognizers.remove(gestureRecognizer)
            gestureRecognizer.view = null
            return@run gestureRecognizers.toList()
        }
    }

    // Accessibility

    // todo: stub
    var isAccessibilityElement: Boolean = false

    // todo: stub
    var accessibilityLabel: String? = null

    // todo: stub
    var accessibilityHint: String? = null

    // todo: stub
    var accessibilityValue: String? = null

    // todo: stub
    var accessibilityIdentifier: String? = null


    // Private

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (this is UIWindow) { return }
        frame?.let {
            val scale = resources.displayMetrics.density
            x = (it.x * scale).toFloat()
            y = (it.y * scale).toFloat()
            setMeasuredDimension((it.width * scale).toInt(), (it.height * scale).toInt())
        }
    }

    private var ignoreTransform = false

    override fun draw(canvas: Canvas?) {
        val canvas = canvas ?: return
        if (canvas !is CAOSCanvas && this.clipsToBounds && !this.transform.isIdentity() && this.isHardwareAccelerated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val bitmap = UIView.createBitmap(((this.frame.width + this.layer.shadowRadius * 2) * scale).toInt(), ((this.frame.height + this.layer.shadowRadius * 2) * scale).toInt())
                UIView.lockBitmap(bitmap)
                val offScreenCtx0 = CAOSCanvas(bitmap)
                offScreenCtx0.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                ignoreTransform = true
                this.draw(offScreenCtx0)
                canvas.save()
                val unmatrix = this.transform.unmatrix()
                val matrix = UIView.sharedMatrix
                matrix.reset()
                matrix.postTranslate(-(this.width / 2.0).toFloat(), -(this.height / 2.0).toFloat())
                matrix.postRotate(unmatrix.degree.toFloat())
                matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
                matrix.postTranslate((unmatrix.translate.x * scale).toFloat(), (unmatrix.translate.y * scale).toFloat())
                matrix.postTranslate((this.width / 2.0).toFloat(), (this.height / 2.0).toFloat())
                canvas.concat(matrix)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                UIView.unlockBitmap(bitmap)
                canvas.restore()
            } catch (e: Exception) { } // avoid OOM crash.
            return // WTF Android >= M && hardwareAccelerated cause clipPath apply canvas transform error.
        }
        canvas.save()
        if (!this.transform.isIdentity() && !ignoreTransform) {
            val unmatrix = this.transform.unmatrix()
            val matrix = UIView.sharedMatrix
            matrix.reset()
            matrix.postTranslate(-(this.width / 2.0).toFloat(), -(this.height / 2.0).toFloat())
            matrix.postRotate(unmatrix.degree.toFloat())
            matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
            matrix.postTranslate((unmatrix.translate.x * scale).toFloat(), (unmatrix.translate.y * scale).toFloat())
            matrix.postTranslate((this.width / 2.0).toFloat(), (this.height / 2.0).toFloat())
            canvas.concat(matrix)
        }
        if (ignoreTransform) { ignoreTransform = true }
        canvas.let {
            this.layer.view = this
            this.layer.drawInContext(it)
        }
        if (!this.clipsToBounds) {
            val rect = canvas.clipBounds
            rect.inset(-100000, -100000)
            canvas.clipRect(rect, Region.Op.REPLACE)
        }
        else {
            canvas.clipPath(this.layer.createBoundsPath())
        }
        super.draw(canvas)
        canvas.restore()
    }

    open fun hitTest(point: CGPoint): UIView? {
        if (this.userInteractionEnabled && this.alpha > 0.0 && !this.hidden && this.pointInside(point)) {
            this.subviews.reversed().forEach {
                val convertedPoint = it.convertPointFromView(point, this)
                it.hitTest(convertedPoint)?.let { return it }
            }
            return this
        }
        return null
    }

    open fun touchesBegan(touches: Set<UITouch>) {
        this.gestureRecognizers.forEach { it.handleTouch(touches) }
        this.superview?.touchesBegan(touches)
    }

    open fun touchesMoved(touches: Set<UITouch>) {
        this.gestureRecognizers.forEach { it.handleTouch(touches) }
        this.superview?.touchesMoved(touches)
    }

    open fun touchesEnded(touches: Set<UITouch>) {
        this.gestureRecognizers.forEach { it.handleTouch(touches) }
        this.superview?.touchesEnded(touches)
    }

    open fun touchesCancelled(touches: Set<UITouch>) {
        this.gestureRecognizers.forEach { it.handleTouch(touches) }
        this.superview?.touchesCancelled(touches)
    }

    open fun pointInside(point: CGPoint): Boolean {
        return point.x >= 0.0 && point.y >= 0.0 && point.x <= this.frame.width && point.y <= this.frame.height
    }

    fun convertPointFromView(point: CGPoint, fromView: UIView): CGPoint {
        val fromPoint = fromView.convertPointToWindow(point) ?: return point
        return this.convertPointFromWindow(fromPoint) ?: return point
    }

    fun convertPointToView(point: CGPoint, toView: UIView): CGPoint {
        return toView.convertPointFromView(point, this)
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
                val unmatrix = it.transform.unmatrix()
                val matrix2 = Matrix()
                matrix2.postTranslate(-(it.frame.width / 2.0).toFloat(), -(it.frame.height / 2.0).toFloat())
                matrix2.postRotate(unmatrix.degree.toFloat())
                matrix2.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
                matrix2.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
                matrix2.postTranslate((it.frame.width / 2.0).toFloat(), (it.frame.height / 2.0).toFloat())
                matrix.postConcat(matrix2)
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
                val unmatrix = it.transform.unmatrix()
                val matrix2 = Matrix()
                matrix2.postTranslate(-(it.frame.width / 2.0).toFloat(), -(it.frame.height / 2.0).toFloat())
                matrix2.postRotate(unmatrix.degree.toFloat())
                matrix2.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
                matrix2.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
                matrix2.postTranslate((it.frame.width / 2.0).toFloat(), (it.frame.height / 2.0).toFloat())
                matrix.preConcat(matrix2)
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

        internal var recognizedGesture: UIGestureRecognizer? = null

        internal var sharedVelocityTracker = VelocityTracker.obtain()

        private val sharedMatrix = Matrix()

        private var sharedBitmaps: MutableList<SoftReference<Bitmap>> = mutableListOf()

        private var lockedBitmaps: WeakHashMap<Bitmap, Boolean> = WeakHashMap()

        internal fun createBitmap(width: Int, height: Int): Bitmap {
            sharedBitmaps.forEach { item ->
                item.get()?.takeIf { !it.isRecycled && lockedBitmaps[it] == false }?.let { sharedBitmap ->
                    if (sharedBitmap.width >= width && sharedBitmap.height >= height) {
                        return sharedBitmap
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(max(256, min(2048, width)), max(256, min(2048, height)), Bitmap.Config.ARGB_8888)
            sharedBitmaps.add(SoftReference(bitmap))
            return bitmap
        }

        internal fun lockBitmap(bitmap: Bitmap) {
            lockedBitmaps[bitmap] = true
        }

        internal fun unlockBitmap(bitmap: Bitmap) {
            lockedBitmaps[bitmap] = false
        }

    }

}

fun KIMIPackage.installUIView() {
    exporter.exportClass(UIView::class.java, "UIView")
    exporter.exportProperty(UIView::class.java, "layer")
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
    exporter.exportProperty(UIView::class.java, "edo_backgroundColor")
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
    exporter.exportProperty(UIView::class.java, "isAccessibilityElement")
    exporter.exportProperty(UIView::class.java, "accessibilityLabel")
    exporter.exportProperty(UIView::class.java, "accessibilityHint")
    exporter.exportProperty(UIView::class.java, "accessibilityValue")
    exporter.exportProperty(UIView::class.java, "accessibilityIdentifier")
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

fun CGAffineTransform.setScaleX(value: Double): CGAffineTransform {
    val unmatrix = this.unmatrix()
    val matrix = Matrix()
    matrix.postRotate(unmatrix.degree.toFloat())
    matrix.postScale(value.toFloat(), unmatrix.scale.y.toFloat())
    matrix.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
    val arr = FloatArray(9)
    matrix.getValues(arr)
    return CGAffineTransform(arr[0].toDouble(), arr[3].toDouble(), arr[1].toDouble(), arr[4].toDouble(), arr[2].toDouble(), arr[5].toDouble())
}

fun CGAffineTransform.setScaleY(value: Double): CGAffineTransform {
    val unmatrix = this.unmatrix()
    val matrix = Matrix()
    matrix.postRotate(unmatrix.degree.toFloat())
    matrix.postScale(unmatrix.scale.x.toFloat(), value.toFloat())
    matrix.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
    val arr = FloatArray(9)
    matrix.getValues(arr)
    return CGAffineTransform(arr[0].toDouble(), arr[3].toDouble(), arr[1].toDouble(), arr[4].toDouble(), arr[2].toDouble(), arr[5].toDouble())
}

fun CGAffineTransform.setDegree(value: Double): CGAffineTransform {
    val unmatrix = this.unmatrix()
    val matrix = Matrix()
    matrix.postRotate(value.toFloat())
    matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
    matrix.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
    val arr = FloatArray(9)
    matrix.getValues(arr)
    return CGAffineTransform(arr[0].toDouble(), arr[3].toDouble(), arr[1].toDouble(), arr[4].toDouble(), arr[2].toDouble(), arr[5].toDouble())
}

fun CGAffineTransform.setTranslateX(value: Double): CGAffineTransform {
    val unmatrix = this.unmatrix()
    val matrix = Matrix()
    matrix.postRotate(unmatrix.degree.toFloat())
    matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
    matrix.postTranslate(value.toFloat(), unmatrix.translate.y.toFloat())
    val arr = FloatArray(9)
    matrix.getValues(arr)
    return CGAffineTransform(arr[0].toDouble(), arr[3].toDouble(), arr[1].toDouble(), arr[4].toDouble(), arr[2].toDouble(), arr[5].toDouble())
}

fun CGAffineTransform.setTranslateY(value: Double): CGAffineTransform {
    val unmatrix = this.unmatrix()
    val matrix = Matrix()
    matrix.postRotate(unmatrix.degree.toFloat())
    matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
    matrix.postTranslate(unmatrix.translate.x.toFloat(), value.toFloat())
    val arr = FloatArray(9)
    matrix.getValues(arr)
    return CGAffineTransform(arr[0].toDouble(), arr[3].toDouble(), arr[1].toDouble(), arr[4].toDouble(), arr[2].toDouble(), arr[5].toDouble())
}