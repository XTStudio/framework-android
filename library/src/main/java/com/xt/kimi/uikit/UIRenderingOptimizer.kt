package com.xt.kimi.uikit

import android.graphics.Path
import com.xt.endo.CGRect
import com.xt.kimi.currentActivity
import java.util.*
import kotlin.math.max
import kotlin.math.min

class UIRenderingOptimizer {

    internal val noNeedToDrawContent: WeakHashMap<UIView, Boolean> = WeakHashMap()
    private var drawnRects: MutableList<CGRect> = mutableListOf()
    private var rootWindow: UIView? = null
    private var noNeedToDrawAnything = false

    fun measure(view: UIView, isRootView: Boolean = false, clips: CGRect? = null) {
        if (isRootView) {
            this.noNeedToDrawContent.keys.forEach {
                if (it != view) {
                    it.invalidate()
                }
            }
            this.noNeedToDrawContent.clear()
            this.drawnRects.clear()
            this.rootWindow = view
            this.noNeedToDrawAnything = false
        }
        view.subviews.reversed().forEach {
            if (!it.edo_isVisible) {
                it.setWillNotDraw(true)
                return@forEach
            }
            this.measure(it, false, kotlin.run {
                if (view.clipsToBounds) {
                    val nextClipsBounds = view.convertRectToWindow(null) ?: return@run clips ?: null
                    return@run clips?.let { clips ->
                        return@run CGRect(
                                max(clips.x, nextClipsBounds.x),
                                max(clips.y, nextClipsBounds.y),
                                min(clips.x + clips.width, nextClipsBounds.x + nextClipsBounds.width) - max(clips.x, nextClipsBounds.x),
                                min(clips.y + clips.height, nextClipsBounds.y + nextClipsBounds.height) - max(clips.y, nextClipsBounds.y)
                        )
                    } ?: nextClipsBounds
                }
                return@run clips ?: null
            })
        }
        val needsDrawn = this.checkNeedsDrawn(view, clips)
        if (!needsDrawn) {
            this.noNeedToDrawContent[view] = true
        }
        if (view.clipsToBounds) {
            view.setWillNotDraw(false)
        }
        else {
            view.setWillNotDraw(!needsDrawn)
        }
    }

    private val checkDrawnSeparateRects: MutableList<CGRect> = mutableListOf()

    private fun checkNeedsDrawn(view: UIView, clips: CGRect? = null): Boolean {
        if (this.noNeedToDrawAnything) {
            return false
        }
        val rootWindow = rootWindow ?: return false
        var windowRect = view.convertRectToWindow(null) ?: return false
        if (!CGRectIntersectsRect(windowRect, rootWindow.bounds)) {
            return false
        }
        clips?.let {
            windowRect = CGRect(
                    max(clips.x, windowRect.x),
                    max(clips.y, windowRect.y),
                    min(clips.x + clips.width, windowRect.x + windowRect.width) - max(clips.x, windowRect.x),
                    min(clips.y + clips.height, windowRect.y + windowRect.height) - max(clips.y, windowRect.y)
            )
        }
        this.checkDrawnSeparateRects.clear()
        this.checkDrawnSeparateRects.add(windowRect)
        var needsDrawn = false
        var nextDrawnRects: MutableList<CGRect> = this.drawnRects.toMutableList()
        var maxLoop = 10
        while (maxLoop > 0 && !needsDrawn && this.checkDrawnSeparateRects.count() > 0) {
            maxLoop--
            this.checkDrawnSeparateRects.toList().forEach { separateRect ->
                var noResult = true
                nextDrawnRects.toList().forEach { drawnRect ->
                    if (!noResult) { return@forEach }
                    if (CGRectContainsRect(drawnRect, separateRect)) {
                        this.checkDrawnSeparateRects.remove(separateRect)
                        noResult = false
                    }
                    else if (CGRectIntersectsRect(separateRect, drawnRect)) {
                        this.devideRect(separateRect, drawnRect)?.forEach {
                            this.checkDrawnSeparateRects.add(it)
                        }
                        this.checkDrawnSeparateRects.remove(separateRect)
                        noResult = false
                    }
                }
                if (noResult) {
                    needsDrawn = true
                    this.checkDrawnSeparateRects.remove(separateRect)
                    nextDrawnRects.add(separateRect)
                }
            }
        }
        if (maxLoop <= 0) {
            needsDrawn = true
        }
        if (needsDrawn && view.edo_isOpaque) {
            this.drawnRects.clear()
            nextDrawnRects.forEach { rect ->
                if (rect.width * rect.height < 200.0) { return@forEach }
                this.drawnRects.add(rect)
            }
        }
        if (!needsDrawn) {
            val containsRects = this.drawnRects.filter { CGRectContainsRect(windowRect, it) }
            if (containsRects.count() > 0) {
                this.drawnRects.removeAll(containsRects)
                this.drawnRects.add(windowRect)
            }
            if (CGRectContainsRect(windowRect, rootWindow.bounds)) {
                this.noNeedToDrawAnything = true
            }
        }
        return needsDrawn
    }

    private val devidedRects: MutableList<CGRect> = mutableListOf()

    private fun devideRect(origin: CGRect, target: CGRect): List<CGRect>? {
        this.devidedRects.clear()
        if (CGRectIntersectsRect(origin, target)) {
            if (target.x - origin.x > 0 && target.y - origin.y > 0) {
                this.devidedRects.add(CGRect(origin.x, origin.y, target.x - origin.x, target.y - origin.y))
            }
            if (target.x + target.width - max(target.x, origin.x) > 0 && target.y - origin.y > 0) {
                this.devidedRects.add(CGRect(max(target.x, origin.x), origin.y, target.x + target.width - max(target.x, origin.x), target.y - origin.y))
            }
            if (origin.x + origin.width - (target.x + target.width) > 0 && target.y - origin.y > 0) {
                this.devidedRects.add(CGRect(target.x + target.width, origin.y, origin.x + origin.width - (target.x + target.width), target.y - origin.y))
            }
            if (target.x - origin.x > 0 && target.y + target.height - max(target.y, origin.y) > 0) {
                this.devidedRects.add(CGRect(origin.x, max(target.y, origin.y), target.x - origin.x, target.y + target.height - max(target.y, origin.y)))
            }
            if (origin.x + origin.width - (target.x + target.width) > 0 && target.y + target.height - max(target.y, origin.y) > 0) {
                this.devidedRects.add(CGRect(target.x + target.width, max(target.y, origin.y), origin.x + origin.width - (target.x + target.width), target.y + target.height - max(target.y, origin.y)))
            }
            if (target.x - origin.x > 0 && origin.y + origin.height - (target.y + target.height) > 0) {
                this.devidedRects.add(CGRect(origin.x, target.y + target.height, target.x - origin.x, origin.y + origin.height - (target.y + target.height)))
            }
            if (target.x + target.width - max(target.x, origin.x) > 0 && origin.y + origin.height - (target.y + target.height) > 0) {
                this.devidedRects.add(CGRect(max(target.x, origin.x), target.y + target.height, target.x + target.width - max(target.x, origin.x), origin.y + origin.height - (target.y + target.height)))
            }
            if (origin.x + origin.width - (target.x + target.width) > 0 && origin.y + origin.height - (target.y + target.height) > 0) {
                this.devidedRects.add(CGRect(target.x + target.width, target.y + target.height, origin.x + origin.width - (target.x + target.width), origin.y + origin.height - (target.y + target.height)))
            }
            return this.devidedRects
        }
        else {
            return null
        }
    }

    companion object {

        val shared = UIRenderingOptimizer()

    }

}