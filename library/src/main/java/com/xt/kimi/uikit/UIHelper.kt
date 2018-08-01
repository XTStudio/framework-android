package com.xt.kimi.uikit

import com.xt.endo.CGRect

fun CGRectIntersectsRect(r1: CGRect, r2: CGRect): Boolean {
    if (r1.x + r1.width <= r2.x ||
        r2.x + r2.width <= r1.x ||
        r1.y + r1.height <= r2.y ||
        r2.y + r2.height <= r1.y) {
        return false
    }
    return true
}