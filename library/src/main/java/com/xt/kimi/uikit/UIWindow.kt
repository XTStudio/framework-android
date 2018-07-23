package com.xt.kimi.uikit

import android.view.MotionEvent
import com.xt.endo.CGPoint

/**
 * Created by cuiminghui on 2018/7/23.
 */
class UIWindow : UIView() {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return false
        val firstView = this.subviews.firstOrNull() ?: return false
        if (event.action == MotionEvent.ACTION_DOWN) {
            val point = CGPoint((event.rawX / scale).toDouble(), (event.rawY / scale).toDouble())
            val target = this.hitTest(point)
            print(true)
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun pointInside(point: CGPoint): Boolean {
        return true
    }

}