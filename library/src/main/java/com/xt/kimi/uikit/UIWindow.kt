package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.SystemClock
import android.view.MotionEvent
import com.xt.endo.CGPoint
import com.xt.endo.CGRect
import com.xt.endo.EDOCallback
import kotlin.math.abs

/**
 * Created by cuiminghui on 2018/7/23.
 */
class UIWindow : UIView() {

    val touches: MutableMap<Int, UITouch> = mutableMapOf()
    val upCount: MutableMap<CGPoint, Int> = mutableMapOf()
    val upTimestamp: MutableMap<CGPoint, Double> = mutableMapOf()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return super.onTouchEvent(event)
        sharedVelocityTracker.addMovement(event)
        if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
            val pointerId = event.getPointerId(event.actionIndex)
            val point = CGPoint((event.getX(event.actionIndex) / scale).toDouble(), (event.getY(event.actionIndex) / scale).toDouble())
            val target = this.hitTest(point)
            if (target is UINativeTouchView) {
                return false
            }
            val touch = kotlin.run {
                val touch = UITouch()
                touches[pointerId] = touch
                return@run touch
            }
            touch.identifier = pointerId
            touch.phase = UITouchPhase.began
            touch.tapCount = kotlin.run {
                upCount.forEach {
                    val timestamp = upTimestamp[it.key] ?: 0.0
                    if (SystemClock.uptimeMillis().toDouble() / 1000.0 - timestamp < 1.0
                            && abs(it.key.x - point.x) < 44.0 && abs(it.key.y - point.y) < 44.0) {
                        return@run it.value + 1
                    }
                }
                return@run 1
            }
            touch.timestamp = SystemClock.uptimeMillis().toDouble() / 1000.0
            touch.window = this
            touch.windowPoint = point
            touch.view = target
            touch.view?.touchesBegan(setOf(touch))
        }
        else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            sharedVelocityTracker.computeCurrentVelocity(1000)
            val pointerId = event.getPointerId(event.actionIndex)
            val point = CGPoint((event.getX(event.actionIndex) / scale).toDouble(), (event.getY(event.actionIndex) / scale).toDouble())
            val touch = touches[pointerId] ?: return true
            touch.phase = UITouchPhase.moved
            touch.timestamp = SystemClock.uptimeMillis().toDouble() / 1000.0
            touch.windowPoint = point
            touch.view?.touchesMoved(setOf(touch))
        }
        else if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_POINTER_UP) {
            val pointerId = event.getPointerId(event.actionIndex)
            val point = CGPoint((event.getX(event.actionIndex) / scale).toDouble(), (event.getY(event.actionIndex) / scale).toDouble())
            val touch = touches[pointerId] ?: return true
            touch.phase = UITouchPhase.ended
            touch.timestamp = SystemClock.uptimeMillis().toDouble() / 1000.0
            touch.windowPoint = point
            touch.view?.touchesEnded(setOf(touch))
            if (event.actionMasked == MotionEvent.ACTION_UP) {
                upCount.clear()
                upTimestamp.clear()
                touches.forEach {
                    it.value.windowPoint?.let { windowPoint ->
                        upCount[windowPoint] = it.value.tapCount
                        upTimestamp[windowPoint] = it.value.timestamp
                    }
                }
                touches.clear()
                sharedVelocityTracker.clear()
            }
        }
        else if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
            val pointerId = event.getPointerId(event.actionIndex)
            val point = CGPoint((event.getX(event.actionIndex) / scale).toDouble(), (event.getY(event.actionIndex) / scale).toDouble())
            val touch = touches[pointerId] ?: return true
            touch.phase = UITouchPhase.cancelled
            touch.timestamp = SystemClock.uptimeMillis().toDouble() / 1000.0
            touch.windowPoint = point
            touch.view?.touchesCancelled(setOf(touch))
            upCount.clear()
            upTimestamp.clear()
            touches.clear()
            sharedVelocityTracker.clear()
        }
        return true
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return super.onInterceptTouchEvent(event)
        val point = CGPoint((event.getX(event.actionIndex) / scale).toDouble(), (event.getY(event.actionIndex) / scale).toDouble())
        val target = this.hitTest(point)
        if (target is UINativeTouchView) {
            return false
        }
        return true
    }

    override fun pointInside(point: CGPoint): Boolean {
        return true
    }

    // Private

    internal var transparentStatusBar = false

    internal var statusBarHeight: Double = 0.0

    internal var softButtonBarHeight: Double = 0.0

    internal var rootViewController: UIViewController? = null
        set(value) {
            field?.let {
                it.window = null
                it.view.removeFromSuperview()
            }
            field = value
            field?.let {
                it.window = this
                this.addSubview(it.view)
            }
        }

    internal var presentedViewControllers: List<UIViewController> = listOf()

    internal fun presentViewController(viewController: UIViewController, animated: Boolean, complete: (() -> Unit)? = null) {
        this.presentedViewControllers = kotlin.run {
            val presentedViewControllers = this.presentedViewControllers.toMutableList()
            presentedViewControllers.add(viewController)
            return@run presentedViewControllers.toList()
        }
        viewController.window = this
        this.addSubview(viewController.view)
        if (animated) {
            viewController.view.frame = CGRect(0.0, this.bounds.height, this.bounds.width, this.bounds.height)
            UIAnimator.shared.bouncy(0.0, 24.0, EDOCallback.createWithBlock {
                viewController.view.frame = this.bounds
            }, EDOCallback.createWithBlock {
                complete?.invoke()
            })
        }
        else {
            viewController.view.frame = this.bounds
            complete?.invoke()
        }
    }

    internal fun dismissViewController(animated: Boolean, complete: (() -> Unit)? = null) {
        if (this.presentedViewControllers.count() > 0) {
            val fromViewController = this.presentedViewControllers[this.presentedViewControllers.count() - 1]
            this.presentedViewControllers = kotlin.run {
                val presentedViewControllers = this.presentedViewControllers.toMutableList()
                presentedViewControllers.remove(fromViewController)
                return@run presentedViewControllers.toList()
            }
            fromViewController.presentingViewController?.presentedViewController = null
            fromViewController.presentingViewController = null
            fromViewController.window = null
            if (animated) {
                UIAnimator.shared.bouncy(0.0, 24.0, EDOCallback.createWithBlock {
                    fromViewController.view.frame = CGRect(0.0, this.bounds.height, this.bounds.width, this.bounds.height)
                }, EDOCallback.createWithBlock {
                    fromViewController.view.removeFromSuperview()
                    complete?.invoke()
                })
            }
            else {
                fromViewController.view.removeFromSuperview()
                complete?.invoke()
            }
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.rootViewController?.let {
            it.view.frame = this.bounds
        }
        this.presentedViewControllers.forEach {
            it.view.frame = this.bounds
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            this.frame = CGRect(0.0, 0.0, (this.width / scale).toDouble(), (this.height / scale).toDouble() - this.softButtonBarHeight)
        }
    }

    private val softButtonBarPaint = Paint()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (this.softButtonBarHeight > 0.0) {
            val canvas = canvas ?: return
            this.softButtonBarPaint.reset()
            this.softButtonBarPaint.color = Color.BLACK
            canvas.drawRect(RectF(0f, (canvas.height - this.softButtonBarHeight * scale).toFloat(), canvas.width.toFloat(), canvas.height.toFloat()), this.softButtonBarPaint)
        }
    }

}