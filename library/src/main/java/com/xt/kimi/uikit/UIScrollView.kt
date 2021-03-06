package com.xt.kimi.uikit

import android.graphics.Canvas
import android.os.Build
import android.view.Choreographer
import android.widget.EdgeEffect
import android.widget.Scroller
import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import kotlin.math.*

internal class UIScrollWrapperView: UIView()

open class UIScrollView: UIView() {

    open var edo_contentOffset: CGPoint = CGPoint(0.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "contentOffset")
            this.contentView.scrollX = (value.x * scale).toInt()
            this.contentView.scrollY = (value.y * scale).toInt()
            this.resetScrollIndicators()
        }

    var contentSize: CGSize = CGSize(0.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "contentSize")
            this.resetLockedDirection()
        }

    var contentInset: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            val deltaX = value.left - field.left
            val deltaY = value.top - field.top
            field = value
            this.setContentOffset(CGPoint(this.edo_contentOffset.x - deltaX, this.edo_contentOffset.y - deltaY), false)
            EDOJavaHelper.valueChanged(this, "contentInset")
            this.resetLockedDirection()
        }

    var directionalLockEnabled = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "directionalLockEnabled")
        }

    var bounces = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "bounces")
        }

    var alwaysBounceVertical = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "alwaysBounceVertical")
        }

    var alwaysBounceHorizontal = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "alwaysBounceHorizontal")
        }

    var pagingEnabled = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "pagingEnabled")
        }

    var scrollEnabled = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "scrollEnabled")
            this.panGestureRecognizer.enabled = value
        }

    var showsHorizontalScrollIndicator = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "showsHorizontalScrollIndicator")
        }

    var showsVerticalScrollIndicator = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "showsVerticalScrollIndicator")
        }

    fun setContentOffset(contentOffset: CGPoint, animated: Boolean) {
        this.scroller.abortAnimation()
        if (this.decelerating) {
            this.didEndDecelerating()
        }
        if (animated) {
            scroller.startScroll(
                    this.edo_contentOffset.x.toInt(),
                    this.edo_contentOffset.y.toInt(),
                    (contentOffset.x - this.edo_contentOffset.x).toInt(),
                    (contentOffset.y - this.edo_contentOffset.y).toInt(),
                    500
            )
            this.loopScrollAnimation(true)
        }
        else {
            this.edo_contentOffset = contentOffset
            this@UIScrollView.didScroll()
        }
    }

    fun scrollRectToVisible(rect: CGRect, animated: Boolean) {
        var targetContentOffset = this.edo_contentOffset
        if (rect.x < this.edo_contentOffset.x) {
            targetContentOffset = CGPoint(rect.x, targetContentOffset.y)
        }
        else if (rect.x + rect.width > this.edo_contentOffset.x + this.bounds.width) {
            targetContentOffset = CGPoint(rect.x + rect.width - this.bounds.width, targetContentOffset.y)
        }
        if (rect.y < this.edo_contentOffset.y) {
            targetContentOffset = CGPoint(targetContentOffset.x, rect.y)
        }
        else if (rect.y + rect.height > this.edo_contentOffset.y + this.bounds.height) {
            targetContentOffset = CGPoint(targetContentOffset.x, rect.y + rect.height - this.bounds.height)
        }
        targetContentOffset = CGPoint(
                max(0.0, min(this.contentSize.width - this.bounds.width, targetContentOffset.x)),
                max(0.0, min(this.contentSize.height - this.bounds.height, targetContentOffset.y))
        )
        this.setContentOffset(targetContentOffset, animated)
    }

    var tracking = false
        private set(value) {
            field = value
            this.resetScrollIndicatorVisibleState()
        }

    var dragging = false
        private set(value) {
            field = value
            this.resetScrollIndicatorVisibleState()
        }

    var decelerating = false
        private set(value) {
            field = value
            this.resetScrollIndicatorVisibleState()
        }

    var scrollsToTop = false

    // Implementation

    private val panGestureRecognizer: UIPanGestureRecognizer

    private val contentView = UIScrollWrapperView()

    private val scroller = Scroller(this.context)

    private var currentLockedDirection: Int? = null

    private val horizontalScrollIndicator: UIView

    private val verticalScrollIndicator: UIView

    override var frame: CGRect
        get() = super.frame
        set(value) {
            super.frame = value
            this.contentView.frame = this.bounds
            this.resetLockedDirection()
        }

    init {
        this.clipsToBounds = true
        this.panGestureRecognizer = object : UIPanGestureRecognizer() {
            override fun handleEvent(name: String) {
                super.handleEvent(name)
                if (name == "began") {
                    this@UIScrollView.deceleratingWasCancelled = false
                    this@UIScrollView.currentLockedDirection = null
                    this.setTranslation(CGPoint(0.0, 0.0), null)
                    this@UIScrollView.willBeginDragging()
                }
                else if (name == "changed") {
                    var translation = this.translationInView(null)
                    if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == null) {
                        if (abs(translation.x) >= 4.0) {
                            this@UIScrollView.currentLockedDirection = 0
                        }
                        else if (abs(translation.y) >= 4.0) {
                            this@UIScrollView.currentLockedDirection = 1
                        }
                        return
                    }
                    else if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == 0) {
                        translation = CGPoint(translation.x, 0.0)
                    }
                    else if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == 1) {
                        translation = CGPoint(0.0, translation.y)
                    }
                    this@UIScrollView.createFetchMoreEffect(translation)
                    val refreshOffset = this@UIScrollView.createRefreshEffect(translation)
                    if (refreshOffset == null) {
                        this@UIScrollView.createBounceEffect(translation, this.locationInView(null))
                    }
                    this@UIScrollView.edo_contentOffset = CGPoint(
                            max(-this@UIScrollView.contentInset.left, min(max(0.0, this@UIScrollView.contentSize.width + this@UIScrollView.contentInset.right - this@UIScrollView.bounds.width), this@UIScrollView.edo_contentOffset.x - translation.x)),
                            max(-this@UIScrollView.contentInset.top - (if(refreshControl?.edo_enabled == true) 240.0 else 0.0), min(max(0.0, this@UIScrollView.contentSize.height + this@UIScrollView.contentInset.bottom - this@UIScrollView.bounds.height), this@UIScrollView.edo_contentOffset.y - (refreshOffset ?: translation.y)))
                    )
                    this.setTranslation(CGPoint(0.0, 0.0), null)
                    this@UIScrollView.didScroll()
                }
                else if (name == "ended") {
                    if (!this@UIScrollView.edgeVerticalEffect.isFinished || !this@UIScrollView.edgeHorizontalEffect.isFinished) {
                        this@UIScrollView.edgeVerticalEffect.onRelease()
                        this@UIScrollView.edgeHorizontalEffect.onRelease()
                        this@UIScrollView.startFinishEdgeAnimation()
                    }
                    var velocity = this.velocityInView(null)
                    if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == null) {
                        velocity = CGPoint(0.0, 0.0)
                    }
                    else if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == 0) {
                        velocity = CGPoint(velocity.x, 0.0)
                    }
                    else if (this@UIScrollView.directionalLockEnabled && this@UIScrollView.currentLockedDirection == 1) {
                        velocity = CGPoint(0.0, velocity.y)
                    }
                    this@UIScrollView.willEndDragging(velocity)
                    if (this@UIScrollView.refreshControl != null && this@UIScrollView.refreshControl!!.animationView.edo_alpha >= 1.0) {
                        this@UIScrollView.didEndDragging(false)
                        this@UIScrollView.willBeginDecelerating()
                        this@UIScrollView.didEndDecelerating()
                        this@UIScrollView.refreshControl!!.beginRefreshing_callFromScrollView()
                        this@UIScrollView.setContentOffset(CGPoint(0.0, -this@UIScrollView.contentInset.top - 44.0), true)
                    }
                    else if (this@UIScrollView.refreshControl != null && this@UIScrollView.refreshControl!!.animationView.edo_alpha > 0.0) {
                        this@UIScrollView.didEndDragging(false)
                        this@UIScrollView.willBeginDecelerating()
                        this@UIScrollView.didEndDecelerating()
                        this@UIScrollView.refreshControl!!.animationView.edo_alpha = 0.0
                        this@UIScrollView.setContentOffset(CGPoint(0.0, -this@UIScrollView.contentInset.top), true)
                    }
                    else if (this@UIScrollView.shouldDecelerating(velocity)) {
                        this@UIScrollView.didEndDragging(true)
                        this@UIScrollView.willBeginDecelerating()
                        this@UIScrollView.startDecelerating(velocity)
                    }
                    else {
                        this@UIScrollView.didEndDragging(false)
                        this@UIScrollView.willBeginDecelerating()
                        this@UIScrollView.didEndDecelerating()
                    }
                }
                else if (name == "cancelled") {

                }
            }
        }
        this.addGestureRecognizer(this.panGestureRecognizer)
        super.addSubview(this.contentView)
        this.horizontalScrollIndicator = UIView()
        this.verticalScrollIndicator = UIView()
        this.setupScrollIndicators()
    }

    private fun resetLockedDirection() {
        val contentWidth = this.contentSize.width + this.contentInset.left + this.contentInset.right
        val contentHeight = this.contentSize.height + this.contentInset.top + this.contentInset.bottom
        if (contentWidth <= this.bounds.width && contentHeight <= this.bounds.height) {
            this.panGestureRecognizer.lockedDirection = 0
        }
        else if (contentWidth <= this.bounds.width) {
            this.panGestureRecognizer.lockedDirection = 1
        }
        else if (contentHeight <= this.bounds.height) {
            this.panGestureRecognizer.lockedDirection = 2
        }
    }

    private var deceleratingWasCancelled = false

    override fun touchesBegan(touches: Set<UITouch>) {
        super.touchesBegan(touches)
        this.deceleratingWasCancelled = false
        if (!this.scroller.isFinished) {
            UIView.recognizedGesture = this.panGestureRecognizer
            this.scroller.abortAnimation()
            this.tracking = true
            if (this.decelerating) {
                this.deceleratingWasCancelled = true
                this.didEndDecelerating()
            }
        }
    }

    override fun touchesEnded(touches: Set<UITouch>) {
        super.touchesEnded(touches)
        this.tracking = false
        if (this.deceleratingWasCancelled && this.pagingEnabled) {
            this.startDecelerating(CGPoint(0.0, 0.0))
        }
        if (this.deceleratingWasCancelled) {
            this.post {
                UIView.recognizedGesture = null
            }
        }
    }

    override fun touchesCancelled(touches: Set<UITouch>) {
        super.touchesCancelled(touches)
        this.tracking = false
        if (this.deceleratingWasCancelled && this.pagingEnabled) {
            this.startDecelerating(CGPoint(0.0, 0.0))
        }
        if (this.deceleratingWasCancelled) {
            this.post {
                UIView.recognizedGesture = null
            }
        }
    }

    open fun didScroll() {
        EDOJavaHelper.emit(this, "didScroll", this)
    }

    open fun willBeginDragging() {
        EDOJavaHelper.emit(this, "willBeginDragging", this)
        this.tracking = true
        this.dragging = true
    }

    open fun willEndDragging(velocity: CGPoint) {
        EDOJavaHelper.emit(this, "willEndDragging", this, velocity)
    }

    open fun didEndDragging(decelerate: Boolean) {
        this.tracking = false
        this.dragging = false
        EDOJavaHelper.emit(this, "didEndDragging", this, decelerate)
    }

    open fun willBeginDecelerating() {
        EDOJavaHelper.emit(this, "willBeginDecelerating", this)
        this.decelerating = true
    }

    open fun didEndDecelerating() {
        this.decelerating = false
        EDOJavaHelper.emit(this, "didEndDecelerating", this)
    }

    open fun didEndScrollingAnimation() {
        EDOJavaHelper.emit(this, "didEndScrollingAnimation", this)
    }

    open fun didScrollToTop() {
        EDOJavaHelper.emit(this, "didScrollToTop", this)
    }

    private fun shouldDecelerating(velocity: CGPoint): Boolean {
        if (velocity.y > 0 && this.edo_contentOffset.y < this.contentSize.height + this.contentInset.bottom - this.bounds.height) {
            return true
        }
        else if (velocity.y < 0 && this.edo_contentOffset.y > -this.contentInset.top) {
            return true
        }
        if (velocity.x > 0 && this.edo_contentOffset.x < this.contentSize.width + this.contentInset.right - this.bounds.width) {
            return true
        }
        else if (velocity.x < 0 && this.edo_contentOffset.x > -this.contentInset.left) {
            return true
        }
        return false
    }

    private fun startDecelerating(velocity: CGPoint) {
        scroller.fling(
                this.edo_contentOffset.x.toInt(),
                this.edo_contentOffset.y.toInt(),
                -velocity.x.toInt(),
                -velocity.y.toInt(),
                -this.contentInset.left.toInt() - 1000,
                (this.contentSize.width + this.contentInset.right - this.bounds.width).toInt() + 1000,
                -this.contentInset.top.toInt() - 1000,
                (this.contentSize.height + this.contentInset.bottom - this.bounds.height).toInt() + 1000
        )
        if (this.pagingEnabled) {
            scroller.abortAnimation()
            val minY = floor(this.edo_contentOffset.y / this.bounds.height) * this.bounds.height
            val maxY = ceil(this.edo_contentOffset.y / this.bounds.height) * this.bounds.height
            val minX = floor(this.edo_contentOffset.x / this.bounds.width) * this.bounds.width
            val maxX = ceil(this.edo_contentOffset.x / this.bounds.width) * this.bounds.width
            scroller.startScroll(
                    this.edo_contentOffset.x.toInt(),
                    this.edo_contentOffset.y.toInt(),
                    ceil(max(minX, min(maxX, (round(scroller.finalX / this.bounds.width) * this.bounds.width))) - this.edo_contentOffset.x).toInt(),
                    ceil(max(minY, min(maxY, (round(scroller.finalY / this.bounds.height) * this.bounds.height))) - this.edo_contentOffset.y).toInt(),
                    500
            )
        }
        this.loopScrollAnimation()
    }

    private fun loopScrollAnimation(ignoreBounds: Boolean = false) {
        val finished = !this.scroller.computeScrollOffset()
        if (!finished) {
            var minY = -this.contentInset.top
            if (refreshControl?.refreshing == true) {
                minY -= 44.0
            }
            if (ignoreBounds && this.contentSize.height != 0.0) {
                minY = -99999.0
            }
            this.edo_contentOffset = CGPoint(
                    Math.max(-this.contentInset.left, Math.min(Math.max(-this.contentInset.left, this.contentSize.width + this.contentInset.right - this.bounds.width), this.scroller.currX.toDouble())),
                    Math.max(minY, Math.min(Math.max(minY, this.contentSize.height + this.contentInset.bottom - this.bounds.height), this.scroller.currY.toDouble()))
            )
            this.didScroll()
            if (this.contentSize.height > this.bounds.height && Math.abs(this.scroller.currY.toDouble() - this.edo_contentOffset.y) > 0.01) {
                this.scroller.forceFinished(true)
                this.didEndDecelerating()
                return
            }
            if (this.contentSize.width > this.bounds.width && Math.abs(this.scroller.currX.toDouble() - this.edo_contentOffset.x) > 0.01) {
                this.scroller.forceFinished(true)
                this.didEndDecelerating()
                return
            }
            Choreographer.getInstance().postFrameCallback { this.loopScrollAnimation() }
        }
        else if (this.decelerating) {
            this.didEndDecelerating()
        }
        else {
            this.didEndScrollingAnimation()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        this.drawBounceEffect(canvas)
    }

    // Bounces

    private val edgeVerticalEffect = EdgeEffect(this.context)

    private var edgeVerticalDirection = 0

    private val edgeHorizontalEffect = EdgeEffect(this.context)

    private var edgeHorizontalDirection = 0

    private fun createBounceEffect(translation: CGPoint, location: CGPoint) {
        if (this.bounces) {
            if (this.alwaysBounceVertical || this.contentSize.height > this.bounds.height) {
                if (this.edo_contentOffset.y - translation.y < -this.contentInset.top) {
                    this.edgeVerticalDirection = 0
                    val delta = -this.contentInset.top - (this.edo_contentOffset.y - translation.y)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.edgeVerticalEffect.onPull(
                                (delta / 256.0).toFloat(),
                                (location.x / this.bounds.width).toFloat()
                        )
                    }
                    else {
                        this.edgeVerticalEffect.onPull((delta / 256.0).toFloat())
                    }
                    this.invalidate()
                }
                else if (this.edo_contentOffset.y - translation.y > this.contentSize.height + this.contentInset.bottom - this.bounds.height) {
                    this.edgeVerticalDirection = 1
                    val delta = (this.edo_contentOffset.y - translation.y) - (this.contentSize.height + this.contentInset.bottom - this.bounds.height)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.edgeVerticalEffect.onPull(
                                (delta / 256.0).toFloat(),
                                (1.0 - location.x / this.bounds.width).toFloat()
                        )
                    }
                    else {
                        this.edgeVerticalEffect.onPull((delta / 256.0).toFloat())
                    }
                    this.invalidate()
                }
                else if (!this.edgeVerticalEffect.isFinished) {
                    this.edgeVerticalEffect.onRelease()
                    this.startFinishEdgeAnimation()
                }
            }
            if (this.alwaysBounceHorizontal || this.contentSize.width > this.bounds.width) {
                if (this.edo_contentOffset.x - translation.x < -this.contentInset.left) {
                    this.edgeHorizontalDirection = 0
                    val delta = -this.contentInset.left - (this.edo_contentOffset.x - translation.x)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.edgeHorizontalEffect.onPull(
                                (delta / 256.0).toFloat(),
                                (location.y / this.bounds.height).toFloat()
                        )
                    }
                    else {
                        this.edgeHorizontalEffect.onPull((delta / 256.0).toFloat())
                    }
                    this.invalidate()
                }
                else if (this.edo_contentOffset.x - translation.x > this.contentSize.width + this.contentInset.right - this.bounds.width) {
                    this.edgeHorizontalDirection = 1
                    val delta = (this.edo_contentOffset.x - translation.x) - (this.contentSize.width + this.contentInset.right - this.bounds.width)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.edgeHorizontalEffect.onPull(
                                (delta / 256.0).toFloat(),
                                (1.0 - location.y / this.bounds.height).toFloat()
                        )
                    }
                    else {
                        this.edgeHorizontalEffect.onPull((delta / 256.0).toFloat())
                    }
                    this.invalidate()
                }
                else if (!this.edgeHorizontalEffect.isFinished) {
                    this.edgeHorizontalEffect.onRelease()
                    this.startFinishEdgeAnimation()
                }
            }
        }
    }

    private fun drawBounceEffect(canvas: Canvas?) {
        val canvas = canvas ?: return
        if (!this.edgeVerticalEffect.isFinished) {
            val restoreCount = canvas.save()
            if (this.edgeVerticalDirection == 1) {
                canvas.translate(-width + 0f, this.height + 0f)
                canvas.rotate(180.0f, this.width.toFloat(), 0f)
            }
            edgeVerticalEffect.setSize(this.width, this.height)
            edgeVerticalEffect.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
        if (!this.edgeHorizontalEffect.isFinished) {
            val restoreCount = canvas.save()
            if (this.edgeHorizontalDirection == 0) {
                canvas.rotate(270f)
                canvas.translate(-height.toFloat(), 0f)
            }
            else if (this.edgeHorizontalDirection == 1) {
                canvas.rotate(90f)
                canvas.translate(0.0f, -width.toFloat())
            }
            edgeHorizontalEffect.setSize(this.height, this.width)
            edgeHorizontalEffect.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
    }

    private fun startFinishEdgeAnimation() {
        if (!this.edgeVerticalEffect.isFinished || !this.edgeHorizontalEffect.isFinished) {
            this.invalidate()
            Choreographer.getInstance().postFrameCallback { this.startFinishEdgeAnimation() }
        }
    }

    // ScrollIndicator

    private fun setupScrollIndicators() {
        this.horizontalScrollIndicator.edo_backgroundColor = UIColor(0x8f / 255.0, 0x8f / 255.0, 0x90 / 255.0, 1.0)
        this.horizontalScrollIndicator.edo_alpha = 0.0
        this.horizontalScrollIndicator.layer.cornerRadius = 1.0
        super.addSubview(this.horizontalScrollIndicator)
        this.verticalScrollIndicator.edo_backgroundColor = UIColor(0x8f / 255.0, 0x8f / 255.0, 0x90 / 255.0, 1.0)
        this.verticalScrollIndicator.edo_alpha = 0.0
        this.verticalScrollIndicator.layer.cornerRadius = 1.0
        super.addSubview(this.verticalScrollIndicator)
    }

    private fun resetScrollIndicatorVisibleState() {
        if (this.tracking || this.dragging || this.decelerating) {
            if (this.showsHorizontalScrollIndicator) {
                this.horizontalScrollIndicator.edo_alpha = 1.0
            }
            if (this.showsVerticalScrollIndicator) {
                this.verticalScrollIndicator.edo_alpha = 1.0
            }
        }
        else {
            if (this.horizontalScrollIndicator.edo_alpha > 0.0) {
                UIAnimator.shared.linear(0.3, EDOCallback.createWithBlock {
                    this.horizontalScrollIndicator.edo_alpha = 0.0
                }, null)
            }
            if (this.verticalScrollIndicator.edo_alpha > 0.0) {
                UIAnimator.shared.linear(0.3, EDOCallback.createWithBlock {
                    this.verticalScrollIndicator.edo_alpha = 0.0
                }, null)
            }
        }
    }

    private fun resetScrollIndicators() {
        val contentWidth = this.contentInset.left + this.contentInset.right + this.contentSize.width
        if (contentWidth > this.bounds.width) {
            val xProgress = (this.edo_contentOffset.x + this.contentInset.left) / (contentWidth - this.bounds.width)
            val xWidth = max(36.0, (this.bounds.width - 8.0) / (contentWidth / (this.bounds.width - 8.0)))
            this.horizontalScrollIndicator.frame = CGRect(
                    4.0 + xProgress * ((this.bounds.width - 8.0) - xWidth),
                    this.bounds.height - 4.0,
                    xWidth,
                    2.0
            )
        }
        else {
            this.horizontalScrollIndicator.frame = CGRect(
                    0.0,
                    this.bounds.height - 4.0,
                    0.0,
                    2.0
            )
        }
        val contentHeight = this.contentInset.top + this.contentInset.bottom + this.contentSize.height
        if (contentHeight > this.bounds.height) {
            val yProgress = (this.edo_contentOffset.y + this.contentInset.top) / (contentHeight - this.bounds.height)
            val yHeight = max(36.0, (this.bounds.height - 8.0) / (contentHeight / (this.bounds.height - 8.0)))
            this.verticalScrollIndicator.frame = CGRect(
                    this.bounds.width - 4.0,
                    4.0 + yProgress * ((this.bounds.height - 8.0) - yHeight),
                    2.0,
                    yHeight
            )
        }
        else {
            this.verticalScrollIndicator.frame = CGRect(
                    this.bounds.width - 4.0,
                    0.0,
                    2.0,
                    0.0
            )
        }
    }

    // RefreshControl

    private var refreshControl: UIRefreshControl? = null
        set(value) {
            field = value
            value?.let {
                super.addSubview(it.animationView)
                it.animationView.frame = CGRect(0.0, 0.0, this.bounds.width, 44.0)
                it.scrollView = this
            }
        }

    private fun createRefreshEffect(translation: CGPoint): Double? {
        refreshControl?.takeIf { it.edo_enabled }?.takeIf { this.contentSize.width <= this.bounds.width }?.let {
            this.refreshControl?.animationView?.let {
                if (it.frame.y != this.contentInset.top) {
                    it.frame = CGRect(it.frame.x, this.contentInset.top, it.frame.width, it.frame.height)
                }
            }
            if (this.edo_contentOffset.y - translation.y < -this.contentInset.top) {
                val progress = max(0.0, min(1.0, (-this.contentInset.top - (this.edo_contentOffset.y - translation.y)) / 88.0))
                this.refreshControl?.animationView?.edo_alpha = progress
                return translation.y / 3.0
            }
            else {
                this.refreshControl?.animationView?.edo_alpha = 0.0
            }
        }
        return null
    }

    // FetchMoreControl

    private var fetchMoreControl: UIFetchMoreControl? = null
        set(value) {
            field = value
            value?.let {
                it.scrollView = this
            }
        }

    private fun createFetchMoreEffect(translation: CGPoint): Boolean {
        fetchMoreControl?.takeIf { it.edo_enabled }?.takeIf { this.contentSize.width <= this.bounds.width }?.let {
            if (it.fetching) {
                return true
            }
            else if (this.edo_contentOffset.y - translation.y > this.contentSize.height + this.contentInset.bottom - this.bounds.height * 2) {
                it.beginFetching()
                return true
            }
        }
        return false
    }

    // Proxy

    val edo_subviews: List<UIView>
        get() { return this.contentView.subviews }

    override fun insertSubviewAtIndex(view: UIView, index: Int) {
        this.contentView.insertSubviewAtIndex(view, index)
    }

    override fun exchangeSubview(index1: Int, index2: Int) {
        this.contentView.exchangeSubview(index1, index2)
    }

    override fun addSubview(view: UIView) {
        if (view is UIRefreshControl) {
            this.refreshControl = view
            return
        }
        if (view is UIFetchMoreControl) {
            this.fetchMoreControl = view
            return
        }
        this.contentView.addSubview(view)
    }

    override fun insertSubviewBelowSubview(view: UIView, belowSubview: UIView) {
        this.contentView.insertSubviewBelowSubview(view, belowSubview)
    }

    override fun insertSubviewAboveSubview(view: UIView, belowSubview: UIView) {
        this.contentView.insertSubviewAboveSubview(view, belowSubview)
    }

    override fun bringSubviewToFront(view: UIView) {
        this.contentView.bringSubviewToFront(view)
    }

    override fun sendSubviewToBack(view: UIView) {
        this.contentView.sendSubviewToBack(view)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.refreshControl?.animationView?.frame = CGRect(0.0, 0.0, this.bounds.width, 44.0)
    }

}

fun KIMIPackage.installUIScrollView() {
    exporter.exportClass(UIScrollView::class.java, "UIScrollView", "UIView")
    exporter.exportProperty(UIScrollView::class.java, "edo_contentOffset", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "contentSize", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "contentInset", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "directionalLockEnabled", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "bounces", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "alwaysBounceVertical", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "alwaysBounceHorizontal", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "pagingEnabled", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "scrollEnabled", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "showsHorizontalScrollIndicator", false, true, true)
    exporter.exportProperty(UIScrollView::class.java, "showsVerticalScrollIndicator", false, true, true)
    exporter.exportMethodToJavaScript(UIScrollView::class.java, "setContentOffset")
    exporter.exportMethodToJavaScript(UIScrollView::class.java, "scrollRectToVisible")
    exporter.exportProperty(UIScrollView::class.java, "edo_subviews", true)
}