package com.xt.kimi.uikit

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.xt.endo.CGPoint
import com.xt.endo.CGRect
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UIRefreshAnimationView: UIView() {

    val leftDot = UIView()
    val midDot = UIView()
    val rightDot = UIView()

    init {
        this.leftDot.edo_alpha = 0.5
        this.leftDot.layer.cornerRadius = 4.0
        this.midDot.edo_alpha = 0.5
        this.midDot.layer.cornerRadius = 4.0
        this.rightDot.edo_alpha = 0.5
        this.rightDot.layer.cornerRadius = 4.0
        this.addSubview(this.leftDot)
        this.addSubview(this.midDot)
        this.addSubview(this.rightDot)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.leftDot.frame = CGRect(this.bounds.width / 2.0 - 4.0 - 28, this.bounds.height / 2.0 - 4.0, 8.0, 8.0)
        this.midDot.frame = CGRect(this.bounds.width / 2.0 - 4.0, this.bounds.height / 2.0 - 4.0, 8.0, 8.0)
        this.rightDot.frame = CGRect(this.bounds.width / 2.0 + 4.0 + 20, this.bounds.height / 2.0 - 4.0, 8.0, 8.0)
    }

    private var currentIdx = 0
    private var animator: ValueAnimator? = null

    fun startAnimation() {
        this.stopAnimation()
        this.currentIdx = 0
        this.doAnimation()
        val animator = ValueAnimator.ofInt(0, 3)
        animator.interpolator = LinearInterpolator()
        animator.duration = 1250
        animator.repeatCount = 9999
        animator.addUpdateListener {
            this.currentIdx = animator.animatedValue as Int
            this.doAnimation()
        }
        animator.start()
        this.animator = animator
    }

    fun doAnimation() {
        this.leftDot.edo_alpha = if (this.currentIdx == 0) 1.0 else 0.5
        this.midDot.edo_alpha = if (this.currentIdx == 1) 1.0 else 0.5
        this.rightDot.edo_alpha = if (this.currentIdx == 2) 1.0 else 0.5
    }

    fun stopAnimation() {
        this.leftDot.edo_alpha = 0.5
        this.midDot.edo_alpha = 0.5
        this.rightDot.edo_alpha = 0.5
        this.animator?.cancel()
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        this.leftDot.edo_backgroundColor = this.tintColor
        this.midDot.edo_backgroundColor = this.tintColor
        this.rightDot.edo_backgroundColor = this.tintColor
    }

}

class UIRefreshControl: UIView() {

    val animationView = UIRefreshAnimationView()
    internal var scrollView: UIScrollView? = null

    init {
        animationView.edo_alpha = 0.0
        this.tintColor = UIColor.gray
    }

    var edo_enabled: Boolean = true

    var refreshing: Boolean = false
        private set

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        this.animationView.tintColor = this.tintColor
    }

    fun beginRefreshing() {
        this.refreshing = true
        this.animationView.startAnimation()
        EDOJavaHelper.emit(this, "refresh", this)
    }

    fun endRefreshing() {
        this.scrollView?.let {
            if (it.edo_contentOffset.y < -it.contentInset.top) {
                it.setContentOffset(CGPoint(0.0, -it.contentInset.top), true)
            }
        }
        this.animationView.edo_alpha = 0.0
        this.animationView.stopAnimation()
        this.refreshing = false
    }

}

fun KIMIPackage.installUIRefreshControl() {
    exporter.exportClass(UIRefreshControl::class.java, "UIRefreshControl", "UIView")
    exporter.exportProperty(UIRefreshControl::class.java, "edo_enabled")
    exporter.exportProperty(UIRefreshControl::class.java, "refreshing")
    exporter.exportMethodToJavaScript(UIRefreshControl::class.java, "beginRefreshing")
    exporter.exportMethodToJavaScript(UIRefreshControl::class.java, "endRefreshing")
}