package com.xt.kimi.uikit

import android.os.SystemClock
import android.view.MotionEvent
import com.xt.endo.CGPoint
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

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun pointInside(point: CGPoint): Boolean {
        return true
    }

}