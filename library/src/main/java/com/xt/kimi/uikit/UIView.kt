package com.xt.kimi.uikit

import android.app.Activity
import android.graphics.*
import android.os.Build
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

    fun attachToActivity(activity: Activity, isKeyWindow: Boolean = true) {
        this.removeFromSuperview()
        val rootView = if (this is UIWindow) this else UIWindow()
        rootView.addSubview(this)
        if (isKeyWindow) {
            rootView.setBackgroundColor(Color.WHITE)
            activity.setContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
        else {
            activity.addContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    fun detachFromActivity() {
        this.window?.removeFromSuperview()
    }

    init {
        clipChildren = false
        clipToPadding = false
        setWillNotDraw(false)

    }

    open val layer = CALayer()

    internal var viewDelegate: UIViewController? = null

    // Geometry

    private var edo_frame_animations: List<UIAnimation>? = null

    open var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        set(value) {
            if (CGRectEqualToRect(field, value)) { return }
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_frame_animations?.forEach { it.cancel() }
                this.edo_frame_animations = null
            }
            UIAnimator.activeAnimator?.takeIf { !UIAnimator.duringAnimationValueSet }?.let {
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
            val boundsChanged = field.width != value.width || field.height != value.height
            field = value
            EDOJavaHelper.valueChanged(this, "frame")
            EDOJavaHelper.valueChanged(this, "bounds")
            EDOJavaHelper.valueChanged(this, "center")
            this.layer.frame = frame
            if (boundsChanged) {
                this.bounds = CGRect(0.0, 0.0, frame.width, frame.height)
            }
            this.setNeedsLayout(boundsChanged)
        }

    var bounds: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
        private set

    open var center: CGPoint
        get() {
            return CGPoint(this.frame.x + this.frame.width / 2.0, this.frame.y + this.frame.height / 2.0)
        }
        set(value) {
            this.frame = CGRect(value.x - this.frame.width / 2.0, value.y - this.frame.height, this.frame.width, this.frame.height)
        }

    private var edo_transform_animations: List<UIAnimation>? = null

    open var transform: CGAffineTransform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_transform_animations?.forEach { it.cancel() }
                this.edo_transform_animations = null
            }
            UIAnimator.activeAnimator?.takeIf { !UIAnimator.duringAnimationValueSet }?.let {
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
            EDOJavaHelper.valueChanged(this, "transform")
            this.setNeedsDisplay()
        }

    var touchAreaInsets: UIEdgeInsets? = null

    // Hierarchy

    var tag: Int = 0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "tag")
        }

    var superview: UIView? = null
        private set(value) {
            field = value
            this.setWillNotDraw(false)
            EDOJavaHelper.valueChanged(this, "superview")
        }

    val window: UIWindow?
        get() {
            (this as? UIWindow)?.let { return this }
            var current: UIView? = superview
            while (current != null) {
                (current as? UIWindow)?.let {
                    return it
                }
                current = current.superview
            }
            return null
        }

    val viewController: UIViewController?
        get() {
            return this.viewDelegate ?: this.superview?.viewController
        }

    open var subviews: List<UIView> = listOf()
        internal set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "subviews")
        }

    open fun removeFromSuperview() {
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
        } ?: kotlin.run {
            (parent as? ViewGroup)?.removeView(this)
        }
    }

    open fun insertSubviewAtIndex(view: UIView, index: Int) {
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

    open fun exchangeSubview(index1: Int, index2: Int) {
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

    open fun addSubview(view: UIView) {
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

    open fun insertSubviewBelowSubview(view: UIView, belowSubview: UIView) {
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

    open fun insertSubviewAboveSubview(view: UIView, belowSubview: UIView) {
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

    open fun bringSubviewToFront(view: UIView) {
        if (subviews.count() <= 1) { return }
        val viewIndex = subviews.indexOf(view)
        if (viewIndex < 0) { return }
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(subviews.removeAt(viewIndex))
            return@run subviews.toList()
        }
        this.removeView(view)
        this.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
    }

    open fun sendSubviewToBack(view: UIView) {
        if (subviews.count() <= 1) { return }
        val viewIndex = subviews.indexOf(view)
        if (viewIndex < 0) { return }
        subviews = kotlin.run {
            val subviews = this.subviews.toMutableList()
            subviews.add(0, subviews.removeAt(viewIndex))
            return@run subviews.toList()
        }
        this.removeView(view)
        this.addView(view, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        this.setNeedsDisplay()
    }

    open fun isDescendantOfView(view: UIView): Boolean {
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

    open fun viewWithTag(tag: Int): UIView? {
        subviews.firstOrNull{ it.tag == tag }?.let { return it }
        subviews.forEach { it.viewWithTag(tag)?.let { return it } }
        return null
    }

    open fun didAddSubview(subview: UIView) {
        EDOJavaHelper.invokeBindedMethod(this, "didAddSubview", subview)
        this.viewDelegate?.didAddSubview(subview)
    }

    open fun willRemoveSubview(subview: UIView) {
        EDOJavaHelper.invokeBindedMethod(this, "willRemoveSubview", subview)
    }

    open fun willMoveToSuperview(newSuperview: UIView?) {
        EDOJavaHelper.invokeBindedMethod(this, "willMoveToSuperview", newSuperview)
    }

    open fun didMoveToSuperview() {
        EDOJavaHelper.invokeBindedMethod(this, "didMoveToSuperview")
        this.tintColorDidChange()
    }

    fun setNeedsLayout(layoutSubviews: Boolean = true) {
        this.requestLayout()
        if (layoutSubviews) {
            this.layoutSubviews()
        }
    }

    fun layoutIfNeeded() {
        this.requestLayout()
        this.layoutSubviews()
    }

    open fun layoutSubviews() {
        this.viewDelegate?.viewWillLayoutSubviews()
        EDOJavaHelper.invokeBindedMethod(this, "layoutSubviews")
        this.viewDelegate?.viewDidLayoutSubviews()
    }

    // Rendering

    fun setNeedsDisplay() {
        this.invalidate()
    }

    open var clipsToBounds: Boolean
        get() {
            return this.layer.masksToBounds
        }
        set(value) {
            this.layer.masksToBounds = value
            this.setNeedsDisplay()
        }

    private var edo_backgroundColor_animations: List<UIAnimation>? = null

    open var edo_backgroundColor: UIColor? = null
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_backgroundColor_animations?.forEach { it.cancel() }
                this.edo_backgroundColor_animations = null
            }
            UIAnimator.activeAnimator?.takeIf { !UIAnimator.duringAnimationValueSet }?.let {
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
            EDOJavaHelper.valueChanged(this, "backgroundColor")
            this.layer.backgroundColor = value
            this.setNeedsDisplay()
        }

    private var edo_alpha_animation: UIAnimation? = null

    open var edo_alpha: Double
        get() {
            return this.alpha.toDouble()
        }
        set(value) {
            if (!UIAnimator.duringAnimationValueSet) {
                this.edo_alpha_animation?.cancel()
                this.edo_alpha_animation = null
            }
            UIAnimator.activeAnimator?.takeIf { !UIAnimator.duringAnimationValueSet }?.let {
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
            EDOJavaHelper.valueChanged(this, "alpha")
        }

    open var hidden: Boolean = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "hidden")
            this.visibility = if (value) View.GONE else View.VISIBLE
            this.setNeedsDisplay()
        }

    open var contentMode: UIViewContentMode = UIViewContentMode.scaleToFill
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

    open var tintColor: UIColor? = null
        get() {
            return field ?: superview?.tintColor ?: UIColor(0.0, 122.0 / 255.0, 1.0, 1.0)
        }
        set(value) {
            field = value
            this.tintColorDidChange()
        }

    open fun tintColorDidChange() {
        EDOJavaHelper.valueChanged(this, "tintColor")
        EDOJavaHelper.invokeBindedMethod(this, "tintColorDidChange")
        subviews.forEach {
            if (it.tintColor == null) {
                it.tintColorDidChange()
            }
        }
    }

    // GestureRecognizers

    open var userInteractionEnabled: Boolean = true

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

    internal open var isImportantNodeForRendering: Boolean = false

    var edo_opaque: Boolean = false
        set(value) {
            field = value
        }

    open internal val edo_isOpaque: Boolean
        get() {
            if (this.edo_opaque) {
                return true
            }
            if (this.clipsToBounds && this.layer.cornerRadius > 0) {
                return false
            }
            return this.edo_backgroundColor?.a == 1.0 && this.edo_alpha == 1.0 && !this.hidden
        }

    internal val edo_isVisible: Boolean
        get() {
            if (this.edo_alpha <= 0.0 || this.hidden) {
                return false
            }
            var current: UIView? = this.superview
            while (current != null) {
                if (this.edo_alpha <= 0.0 || this.hidden) {
                    return false
                }
                current = current.superview
            }
            return true
        }

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
            if (UIRenderingOptimizer.shared.noNeedToDrawContent[this] != true) {
                this.layer.drawInContext(it)
            }
        }
        if (!this.clipsToBounds) {
//            val rect = canvas.clipBounds
//            rect.inset(-100000, -100000)
//            canvas.clipRect(rect, Region.Op.REPLACE)
        }
        else {
            canvas.clipPath(this.layer.createBoundsPath())
        }
        if (this.childCount > 0) {
            super.draw(canvas)
        }
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
        this.gestureRecognizers.filter { it.enabled }.forEach { it.handleTouch(touches) }
        this.superview?.touchesBegan(touches)
    }

    open fun touchesMoved(touches: Set<UITouch>) {
        this.gestureRecognizers.filter { it.enabled }.forEach { it.handleTouch(touches) }
        this.superview?.touchesMoved(touches)
    }

    open fun touchesEnded(touches: Set<UITouch>) {
        this.gestureRecognizers.filter { it.enabled }.forEach { it.handleTouch(touches) }
        this.superview?.touchesEnded(touches)
    }

    open fun touchesCancelled(touches: Set<UITouch>) {
        this.gestureRecognizers.filter { it.enabled }.forEach { it.handleTouch(touches) }
        this.superview?.touchesCancelled(touches)
    }

    open fun pointInside(point: CGPoint): Boolean {
        touchAreaInsets?.let { touchAreaInsets ->
            return point.x >= 0.0 - touchAreaInsets.left &&
                    point.y >= 0.0 - touchAreaInsets.top &&
                    point.x <= this.frame.width + touchAreaInsets.right &&
                    point.y <= this.frame.height + touchAreaInsets.bottom
        }
        return point.x >= 0.0 && point.y >= 0.0 && point.x <= this.frame.width && point.y <= this.frame.height
    }

    fun convertPointToView(point: CGPoint, toView: UIView): CGPoint {
        return toView.convertPointFromView(point, this)
    }

    fun convertPointFromView(point: CGPoint, fromView: UIView): CGPoint {
        val fromPoint = fromView.convertPointToWindow(point) ?: return point
        return this.convertPointFromWindow(fromPoint) ?: return point
    }

    fun convertRectToView(rect: CGRect, toView: UIView): CGRect {
        val lt = this.convertPointToView(CGPoint(rect.x, rect.y), toView)
        val rt = this.convertPointToView(CGPoint(rect.x + rect.width, rect.y), toView)
        val lb = this.convertPointToView(CGPoint(rect.x, rect.y + rect.height), toView)
        val rb = this.convertPointToView(CGPoint(rect.x + rect.width, rect.y + rect.height), toView)
        return CGRect(
                Math.min(Math.min(lt.x, rt.x), Math.min(lb.x, rb.x)),
                Math.min(Math.min(lt.y, rt.y), Math.min(lb.y, rb.y)),
                Math.max(Math.max(lt.x, rt.x), Math.max(lb.x, rb.x)) - Math.min(Math.min(lt.x, rt.x), Math.min(lb.x, rb.x)),
                Math.max(Math.max(lt.y, rt.y), Math.max(lb.y, rb.y)) - Math.min(Math.min(lt.y, rt.y), Math.min(lb.y, rb.y))
        )
    }

    fun convertRectFromView(rect: CGRect, fromView: UIView): CGRect {
        return fromView.convertRectToView(rect, this)
    }

    fun convertPointToWindow(point: CGPoint): CGPoint? {
        if (this.window == null) {
            return null
        }
        var current: UIView? = this
        val currentPoint = CGPointMutable(point.x, point.y)
        val sharedFloatArr = FloatArray(9)
        while (current != null) {
            if (current is UIWindow) { break }
            if (!current.transform.isIdentity()) {
                val unmatrix = current.transform.unmatrix()
                val matrix = sharedMatrix
                matrix.reset()
                matrix.postTranslate(-(current.frame.width / 2.0).toFloat(), -(current.frame.height / 2.0).toFloat())
                matrix.postRotate(unmatrix.degree.toFloat())
                matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
                matrix.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
                matrix.postTranslate((current.frame.width / 2.0).toFloat(), (current.frame.height / 2.0).toFloat())
                val x = currentPoint.x
                val y = currentPoint.y
                matrix.getValues(sharedFloatArr)
                currentPoint.x = x * sharedFloatArr[0] + y * sharedFloatArr[1] + sharedFloatArr[2]
                currentPoint.y = x * sharedFloatArr[3] + y * sharedFloatArr[4] + sharedFloatArr[5]
            }
            if (current.superview is UIScrollWrapperView) {
                (current.superview as? UIScrollWrapperView)?.let {
                    currentPoint.x += it.scrollX / scale
                    currentPoint.y += it.scrollY / scale
                }
            }
            else {
                currentPoint.x += current.frame.x
                currentPoint.y += current.frame.y
            }
            current = current.superview
        }
        return CGPoint(currentPoint.x, currentPoint.y)
    }

    fun convertPointFromWindow(point: CGPoint): CGPoint? {
        if (this.window == null) {
            return null
        }
        var current: UIView? = this
        val currentPoint = CGPointMutable(point.x, point.y)
        val sharedFloatArr = FloatArray(9)
        var routes: MutableList<UIView> = mutableListOf()
        while (current != null) {
            if (current is UIWindow) { break }
            routes.add(0, current)
            current = current.superview
        }
        routes.forEach {
            if (it.superview is UIScrollWrapperView) {
                (it.superview as? UIScrollWrapperView)?.let {
                    currentPoint.x -= it.scrollX / scale
                    currentPoint.y -= it.scrollY / scale
                }
            }
            else {
                currentPoint.x -= it.frame.x
                currentPoint.y -= it.frame.y
            }
            if (!it.transform.isIdentity()) {
                val unmatrix = it.transform.unmatrix()
                val matrix = sharedMatrix
                matrix.reset()
                matrix.postTranslate(-(it.frame.width / 2.0).toFloat(), -(it.frame.height / 2.0).toFloat())
                matrix.postRotate(unmatrix.degree.toFloat())
                matrix.postScale(unmatrix.scale.x.toFloat(), unmatrix.scale.y.toFloat())
                matrix.postTranslate(unmatrix.translate.x.toFloat(), unmatrix.translate.y.toFloat())
                matrix.postTranslate((it.frame.width / 2.0).toFloat(), (it.frame.height / 2.0).toFloat())
                matrix.getValues(sharedFloatArr)
                val id = 1.0 / ((sharedFloatArr[0] * sharedFloatArr[4]) + (sharedFloatArr[1] * -sharedFloatArr[3]))
                val x = currentPoint.x
                val y = currentPoint.y
                currentPoint.x = (sharedFloatArr[4] * id * x) + (-sharedFloatArr[1] * id * y) + (((sharedFloatArr[5] * sharedFloatArr[1]) - (sharedFloatArr[2] * sharedFloatArr[4])) * id)
                currentPoint.y = (sharedFloatArr[0] * id * y) + (-sharedFloatArr[3] * id * x) + (((-sharedFloatArr[5] * sharedFloatArr[0]) + (sharedFloatArr[2] * sharedFloatArr[3])) * id)
            }
        }
        return CGPoint(currentPoint.x, currentPoint.y)
    }

    fun convertRectToWindow(rect: CGRect?): CGRect? {
        this.window?.let { window ->
            return this.convertRectToView(rect ?: this.bounds, window)
        }
        return null
    }

    open fun intrinsicContentSize(): CGSize? {
        return null
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
                    if (sharedBitmap.width == width && sharedBitmap.height == height) {
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

        private val _convertRectToWindow_sharedMatrix1 = Matrix()
        private val _convertRectToWindow_sharedMatrix2 = Matrix()
        private val _convertRectToWindow_sharedFloatArray = FloatArray(9)

    }

}

fun KIMIPackage.installUIView() {
    exporter.exportClass(UIView::class.java, "UIView")
    exporter.exportProperty(UIView::class.java, "layer", true, true)
    exporter.exportProperty(UIView::class.java, "frame", false, true, true)
    exporter.exportProperty(UIView::class.java, "bounds", false, true)
    exporter.exportProperty(UIView::class.java, "center", false, true)
    exporter.exportProperty(UIView::class.java, "transform", false, true, true)
    exporter.exportProperty(UIView::class.java, "touchAreaInsets", false, true, true)
    exporter.exportMethodToJavaScript(UIView::class.java, "convertPointToView")
    exporter.exportMethodToJavaScript(UIView::class.java, "convertPointFromView")
    exporter.exportMethodToJavaScript(UIView::class.java, "convertRectToView")
    exporter.exportMethodToJavaScript(UIView::class.java, "convertRectFromView")
    exporter.exportProperty(UIView::class.java, "tag", false, true, true)
    exporter.exportProperty(UIView::class.java, "superview", true, true)
    exporter.exportProperty(UIView::class.java, "subviews", true, true)
    exporter.exportProperty(UIView::class.java, "window", true, false)
    exporter.exportProperty(UIView::class.java, "viewController", true, false)
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
    exporter.exportProperty(UIView::class.java, "edo_backgroundColor", false, true, true)
    exporter.exportProperty(UIView::class.java, "edo_alpha", false, true, true)
    exporter.exportProperty(UIView::class.java, "edo_opaque")
    exporter.exportProperty(UIView::class.java, "hidden", false, true, true)
    exporter.exportProperty(UIView::class.java, "contentMode")
    exporter.exportProperty(UIView::class.java, "tintColor", false, true, true)
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
    exporter.exportScript(UIView::class.java, "var UIRectZero={x:0,y:0,width:0,height:0};var UIRectMake=function(x,y,width,height){return{x:x,y:y,width:width,height:height}};var UIRectEqualToRect=function(a,b){return Math.abs(a.x-b.x)<.001&&Math.abs(a.y-b.y)<.001&&Math.abs(a.width-b.width)<.001&&Math.abs(a.height-b.height)<.001};var UIRectInset=function(rect,dx,dy){return{x:rect.x+dx,y:rect.y+dy,width:rect.width-2*dx,height:rect.height-2*dy}};var UIRectOffset=function(rect,dx,dy){return{x:rect.x+dx,y:rect.y+dy,width:rect.width,height:rect.height}};var UIRectContainsPoint=function(rect,point){return point.x>=rect.x&&point.x<=rect.x+rect.width&&point.y>=rect.y&&point.y<=rect.x+rect.height};var UIRectContainsRect=function(rect1,rect2){return UIRectContainsPoint(rect1,{x:rect2.x,y:rect2.y})&&UIRectContainsPoint(rect1,{x:rect2.x+rect2.width,y:rect2.y})&&UIRectContainsPoint(rect1,{x:rect2.x,y:rect2.y+rect2.height})&&UIRectContainsPoint(rect1,{x:rect2.x+rect2.width,y:rect2.y+rect2.height})};var UIRectIntersectsRect=function(a,b){if(a.x+a.width-.1<=b.x||b.x+b.width-.1<=a.x||a.y+a.height-.1<=b.y||b.y+b.height-.1<=a.y){return false}return true};var UIRectUnion=function(r1,r2){var x=Math.min(r1.x,r2.x);var y=Math.min(r1.y,r2.y);var width=Math.max(r1.x+r1.width,r2.x+r2.width);var height=Math.max(r1.y+r1.height,r2.y+r2.height);return{x:x,y:y,width:width,height:height}};var UIRectIsEmpty=function(rect){returnrect.width==0||rect.height==0};var UIPointZero={x:0,y:0};var UIPointMake=function(x,y){return{x:x,y:y}};var UIPointEqualToPoint=function(point1,point2){return Math.abs(point1.x-point2.x)<.001&&Math.abs(point1.y-point2.y)<.001};var UISizeZero={width:0,height:0};var UISizeMake=function(width,height){return{width:width,height:height}};var UISizeEqualToSize=function(a,b){return Math.abs(a.width-b.width)<.001&&Math.abs(a.height-b.height)<.001};var MatrixAlgorithm=function(){function MatrixAlgorithm(){this.props=[];this.props[0]=1;this.props[1]=0;this.props[2]=0;this.props[3]=0;this.props[4]=0;this.props[5]=1;this.props[6]=0;this.props[7]=0;this.props[8]=0;this.props[9]=0;this.props[10]=1;this.props[11]=0;this.props[12]=0;this.props[13]=0;this.props[14]=0;this.props[15]=1}MatrixAlgorithm.prototype.rotate=function(angle){if(angle===0){return this}var mCos=Math.cos(angle);var mSin=Math.sin(angle);return this._t(mCos,-mSin,0,0,mSin,mCos,0,0,0,0,1,0,0,0,0,1)};MatrixAlgorithm.prototype.rotateX=function(angle){if(angle===0){return this}var mCos=Math.cos(angle);var mSin=Math.sin(angle);return this._t(1,0,0,0,0,mCos,-mSin,0,0,mSin,mCos,0,0,0,0,1)};MatrixAlgorithm.prototype.rotateY=function(angle){if(angle===0){return this}var mCos=Math.cos(angle);var mSin=Math.sin(angle);return this._t(mCos,0,mSin,0,0,1,0,0,-mSin,0,mCos,0,0,0,0,1)};MatrixAlgorithm.prototype.rotateZ=function(angle){if(angle===0){return this}var mCos=Math.cos(angle);var mSin=Math.sin(angle);return this._t(mCos,-mSin,0,0,mSin,mCos,0,0,0,0,1,0,0,0,0,1)};MatrixAlgorithm.prototype.shear=function(sx,sy){return this._t(1,sy,sx,1,0,0)};MatrixAlgorithm.prototype.skew=function(ax,ay){return this.shear(Math.tan(ax),Math.tan(ay))};MatrixAlgorithm.prototype.skewFromAxis=function(ax,angle){var mCos=Math.cos(angle);var mSin=Math.sin(angle);return this._t(mCos,mSin,0,0,-mSin,mCos,0,0,0,0,1,0,0,0,0,1);this._t(1,0,0,0,Math.tan(ax),1,0,0,0,0,1,0,0,0,0,1);this._t(mCos,-mSin,0,0,mSin,mCos,0,0,0,0,1,0,0,0,0,1)};MatrixAlgorithm.prototype.scale=function(sx,sy,sz){sz=isNaN(sz)?1:sz;if(sx==1&&sy==1&&sz==1){return this}return this._t(sx,0,0,0,0,sy,0,0,0,0,sz,0,0,0,0,1)};MatrixAlgorithm.prototype.setTransform=function(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p){this.props[0]=a;this.props[1]=b;this.props[2]=c;this.props[3]=d;this.props[4]=e;this.props[5]=f;this.props[6]=g;this.props[7]=h;this.props[8]=i;this.props[9]=j;this.props[10]=k;this.props[11]=l;this.props[12]=m;this.props[13]=n;this.props[14]=o;this.props[15]=p;return this};MatrixAlgorithm.prototype.translate=function(tx,ty,tz){tz=isNaN(tz)?0:tz;if(tx!==0||ty!==0||tz!==0){return this._t(1,0,0,0,0,1,0,0,0,0,1,0,tx,ty,tz,1)}return this};MatrixAlgorithm.prototype._t=function(a2,b2,c2,d2,e2,f2,g2,h2,i2,j2,k2,l2,m2,n2,o2,p2){this.transform(a2,b2,c2,d2,e2,f2,g2,h2,i2,j2,k2,l2,m2,n2,o2,p2)};MatrixAlgorithm.prototype.transform=function(a2,b2,c2,d2,e2,f2,g2,h2,i2,j2,k2,l2,m2,n2,o2,p2){if(a2===1&&b2===0&&c2===0&&d2===0&&e2===0&&f2===1&&g2===0&&h2===0&&i2===0&&j2===0&&k2===1&&l2===0){if(m2!==0||n2!==0||o2!==0){this.props[12]=this.props[12]*a2+this.props[13]*e2+this.props[14]*i2+this.props[15]*m2;this.props[13]=this.props[12]*b2+this.props[13]*f2+this.props[14]*j2+this.props[15]*n2;this.props[14]=this.props[12]*c2+this.props[13]*g2+this.props[14]*k2+this.props[15]*o2;this.props[15]=this.props[12]*d2+this.props[13]*h2+this.props[14]*l2+this.props[15]*p2}return this}var a1=this.props[0];var b1=this.props[1];var c1=this.props[2];var d1=this.props[3];var e1=this.props[4];var f1=this.props[5];var g1=this.props[6];var h1=this.props[7];var i1=this.props[8];var j1=this.props[9];var k1=this.props[10];var l1=this.props[11];var m1=this.props[12];var n1=this.props[13];var o1=this.props[14];var p1=this.props[15];this.props[0]=a1*a2+b1*e2+c1*i2+d1*m2;this.props[1]=a1*b2+b1*f2+c1*j2+d1*n2;this.props[2]=a1*c2+b1*g2+c1*k2+d1*o2;this.props[3]=a1*d2+b1*h2+c1*l2+d1*p2;this.props[4]=e1*a2+f1*e2+g1*i2+h1*m2;this.props[5]=e1*b2+f1*f2+g1*j2+h1*n2;this.props[6]=e1*c2+f1*g2+g1*k2+h1*o2;this.props[7]=e1*d2+f1*h2+g1*l2+h1*p2;this.props[8]=i1*a2+j1*e2+k1*i2+l1*m2;this.props[9]=i1*b2+j1*f2+k1*j2+l1*n2;this.props[10]=i1*c2+j1*g2+k1*k2+l1*o2;this.props[11]=i1*d2+j1*h2+k1*l2+l1*p2;this.props[12]=m1*a2+n1*e2+o1*i2+p1*m2;this.props[13]=m1*b2+n1*f2+o1*j2+p1*n2;this.props[14]=m1*c2+n1*g2+o1*k2+p1*o2;this.props[15]=m1*d2+n1*h2+o1*l2+p1*p2;return this};MatrixAlgorithm.prototype.clone=function(matr){var i;for(i=0;i<16;i+=1){matr.props[i]=this.props[i]}};MatrixAlgorithm.prototype.cloneFromProps=function(props){var i;for(i=0;i<16;i+=1){this.props[i]=props[i]}};MatrixAlgorithm.prototype.applyToPoint=function(x,y,z){return{x:x*this.props[0]+y*this.props[4]+z*this.props[8]+this.props[12],y:x*this.props[1]+y*this.props[5]+z*this.props[9]+this.props[13],z:x*this.props[2]+y*this.props[6]+z*this.props[10]+this.props[14]}};MatrixAlgorithm.prototype.applyToX=function(x,y,z){return x*this.props[0]+y*this.props[4]+z*this.props[8]+this.props[12]};MatrixAlgorithm.prototype.applyToY=function(x,y,z){return x*this.props[1]+y*this.props[5]+z*this.props[9]+this.props[13]};MatrixAlgorithm.prototype.applyToZ=function(x,y,z){return x*this.props[2]+y*this.props[6]+z*this.props[10]+this.props[14]};MatrixAlgorithm.prototype.applyToPointArray=function(x,y,z){return[x*this.props[0]+y*this.props[4]+z*this.props[8]+this.props[12],x*this.props[1]+y*this.props[5]+z*this.props[9]+this.props[13],x*this.props[2]+y*this.props[6]+z*this.props[10]+this.props[14]]};MatrixAlgorithm.prototype.applyToPointStringified=function(x,y){return Math.round(x*this.props[0]+y*this.props[4]+this.props[12])+\",\"+Math.round(x*this.props[1]+y*this.props[5]+this.props[13])};return MatrixAlgorithm}();var Matrix=function(){function Matrix(a,b,c,d,tx,ty){if(a===void 0){a=1}if(b===void 0){b=0}if(c===void 0){c=0}if(d===void 0){d=1}if(tx===void 0){tx=0}if(ty===void 0){ty=0}this.a=a;this.b=b;this.c=c;this.d=d;this.tx=tx;this.ty=ty}Matrix.unmatrix=function(matrix){var A=matrix.a;var B=matrix.b;var C=matrix.c;var D=matrix.d;if(A*D==B*C){return{scale:{x:1,y:1},degree:0,translate:{x:0,y:0}}}var scaleX=Math.sqrt(A*A+B*B);A/=scaleX;B/=scaleX;var skew=A*C+B*D;C-=A*skew;D-=B*skew;var scaleY=Math.sqrt(C*C+D*D);C/=scaleY;D/=scaleY;skew/=scaleY;if(A*D<B*C){A=-A;B=-B;skew=-skew;scaleX=-scaleX}return{scale:{x:scaleX,y:scaleY},degree:Math.atan2(B,A)/(Math.PI/180),translate:{x:matrix.tx,y:matrix.ty}}};Matrix.prototype.setValues=function(values){this.a=values.a;this.b=values.b;this.c=values.c;this.d=values.d;this.tx=values.tx;this.ty=values.ty};Matrix.prototype.getValues=function(){return{a:this.a,b:this.b,c:this.c,d:this.d,tx:this.tx,ty:this.ty}};Matrix.prototype.isIdentity=function(){return this.a==1&&this.b==0&&this.c==0&&this.d==1&&this.tx==0&&this.ty==0};Matrix.prototype.setScale=function(x,y){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-(unMatrix.degree*Math.PI/180));obj.scale(x||unMatrix.scale.x,y||unMatrix.scale.y,1);obj.translate(unMatrix.translate.x,unMatrix.translate.y,0);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.postScale=function(x,y){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-(unMatrix.degree*Math.PI/180));obj.scale(unMatrix.scale.x,unMatrix.scale.y,1);obj.translate(unMatrix.translate.x,unMatrix.translate.y,0);obj.scale(x||1,y||1,1);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.setTranslate=function(x,y){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-(unMatrix.degree*Math.PI/180));obj.scale(unMatrix.scale.x,unMatrix.scale.y,1);obj.translate(x||unMatrix.translate.x,y||unMatrix.translate.y,0);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.postTranslate=function(x,y){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-(unMatrix.degree*Math.PI/180));obj.scale(unMatrix.scale.x,unMatrix.scale.y,1);obj.translate(unMatrix.translate.x,unMatrix.translate.y,0);obj.translate(x||0,y||0,0);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.setRotate=function(angle){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-angle||-(unMatrix.degree*Math.PI/180));obj.scale(unMatrix.scale.x,unMatrix.scale.y,1);obj.translate(unMatrix.translate.x,unMatrix.translate.y,0);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.postRotate=function(angle){var obj=new MatrixAlgorithm;var unMatrix=Matrix.unmatrix(this);obj.rotate(-(unMatrix.degree*Math.PI/180));obj.scale(unMatrix.scale.x,unMatrix.scale.y,1);obj.translate(unMatrix.translate.x,unMatrix.translate.y,0);obj.rotate(-angle);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.preConcat=function(preMatrix){var obj=new MatrixAlgorithm;obj.props[0]=preMatrix.a;obj.props[1]=preMatrix.b;obj.props[4]=preMatrix.c;obj.props[5]=preMatrix.d;obj.props[12]=preMatrix.tx;obj.props[13]=preMatrix.ty;obj.transform(this.a,this.b,0,0,this.c,this.d,0,0,0,0,1,0,this.tx,this.ty,0,1);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};Matrix.prototype.concat=function(postMatrix){var obj=new MatrixAlgorithm;obj.props[0]=this.a;obj.props[1]=this.b;obj.props[4]=this.c;obj.props[5]=this.d;obj.props[12]=this.tx;obj.props[13]=this.ty;obj.transform(postMatrix.a,postMatrix.b,0,0,postMatrix.c,postMatrix.d,0,0,0,0,1,0,postMatrix.tx,postMatrix.ty,0,1);this.a=obj.props[0];this.b=obj.props[1];this.c=obj.props[4];this.d=obj.props[5];this.tx=obj.props[12];this.ty=obj.props[13]};return Matrix}();var UIAffineTransformIdentity={a:1,b:0,c:0,d:1,tx:0,ty:0};var UIAffineTransformMake=function(a,b,c,d,tx,ty){return{a:a,b:b,c:c,d:d,tx:tx,ty:ty}};var UIAffineTransformMakeTranslation=function(tx,ty){return UIAffineTransformMake(1,0,0,1,tx,ty)};var UIAffineTransformMakeScale=function(sx,sy){return UIAffineTransformMake(sx,0,0,sy,0,0)};var UIAffineTransformMakeRotation=function(angle){var mCos=Math.cos(angle);var mSin=Math.sin(angle);return UIAffineTransformMake(mCos,-mSin,mSin,mCos,0,0)};var UIAffineTransformTranslate=function(t,tx,ty){var matrix=new Matrix;matrix.setValues(t);matrix.postTranslate(tx,ty);return matrix.getValues()};var UIAffineTransformScale=function(t,sx,sy){var matrix=new Matrix;matrix.setValues(t);matrix.postScale(sx,sx);return matrix.getValues()};var UIAffineTransformRotate=function(t,angle){var matrix=new Matrix;matrix.setValues(t);matrix.postRotate(angle);return matrix.getValues()};var UIAffineTransformInvert=function(t){return{a:t.a,b:t.c,c:t.b,d:t.d,tx:t.tx,ty:t.ty}};var UIAffineTransformConcat=function(t1,t2){var matrix1=new Matrix;matrix1.setValues(t1);var matrix2=new Matrix;matrix2.setValues(t2);matrix1.concat(matrix2);return matrix1.getValues()};var UIAffineTransformEqualToTransform=function(t1,t2){return Math.abs(t1.a-t2.a)<.001&&Math.abs(t1.b-t2.b)<.001&&Math.abs(t1.c-t2.c)<.001&&Math.abs(t1.d-t2.d)<.001&&Math.abs(t1.tx-t2.tx)<.001&&Math.abs(t1.ty-t2.ty)<.001};var UIAffineTransformIsIdentity=function(transform){return UIAffineTransformEqualToTransform(transform,UIAffineTransformIdentity)};var UIEdgeInsetsZero={top:0,left:0,bottom:0,right:0};var UIEdgeInsetsMake=function(top,left,bottom,right){return{top:top,left:left,bottom:bottom,right:right}};var UIEdgeInsetsInsetRect=function(rect,insets){return{x:rect.x+insets.left,y:rect.y+insets.top,width:rect.width-insets.left-insets.right,height:rect.height-insets.top-insets.bottom}};var UIEdgeInsetsEqualToEdgeInsets=function(rect1,rect2){return Math.abs(rect1.top-rect2.top)<.001&&Math.abs(rect1.left-rect2.left)<.001&&Math.abs(rect1.bottom-rect2.bottom)<.001&&Math.abs(rect1.right-rect2.right)<.001};var UIRangeMake=function(location,length){return{location:location,length:length}};", false)
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