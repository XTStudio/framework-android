package com.xt.kimi.uikit

import android.view.MotionEvent

/**
 * Created by cuiminghui on 2018/7/23.
 */
class UIWindow : UIView() {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

}