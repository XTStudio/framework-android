package com.xt.kimi.uikit

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.facebook.rebound.*
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

interface UIAnimation {

    fun setStartValue(value: Double)
    fun setEndValue(value: Double)
    fun setUpdateListener(listener: (value: Double) -> Unit)
    fun cancel()

}

class LinearAnimation(private val animator: ValueAnimator): UIAnimation {

    private var startValue: Double? = null
    private var endValue: Double? = null

    override fun setStartValue(value: Double) {
        startValue = value
    }

    override fun setEndValue(value: Double) {
        endValue = value
        animator.setFloatValues((startValue ?: 0.0).toFloat(), (endValue ?: 0.0).toFloat())
        animator.start()
    }

    override fun setUpdateListener(listener: (value: Double) -> Unit) {
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            listener(value.toDouble())
        }
    }

    override fun cancel() {
        animator.cancel()
    }

}

class SpringAnimation(private val spring: Spring): UIAnimation {

    override fun setStartValue(value: Double) {
        spring.currentValue = value
    }

    override fun setEndValue(value: Double) {
        spring.endValue = value
    }

    override fun setUpdateListener(listener: (value: Double) -> Unit) {
        spring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring?) {
                super.onSpringUpdate(spring)
                spring?.currentValue?.let {
                    listener(it)
                }
            }
        })
    }

    override fun cancel() {
        spring.destroy()
    }

}

class UIAnimator {

    private val springSystem = SpringSystem.create()

    var animationCreater: (() -> UIAnimation)? = null
        private set

    fun curve(duration: Double, animations: EDOCallback, completion: EDOCallback?) {
        UIAnimator.activeAnimator = this
        var completed = false
        this.animationCreater = animationCreater@{
            val animator = ValueAnimator()
            animator.duration = (duration * 1000).toLong()
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if (!completed) {
                        completed = true
                        completion?.invoke()
                    }
                }
                override fun onAnimationStart(animation: Animator?) { }
                override fun onAnimationCancel(animation: Animator?) { }
            })
            return@animationCreater LinearAnimation(animator)
        }
        animations.invoke()
        UIAnimator.activeAnimator = null
    }

    fun linear(duration: Double, animations: EDOCallback, completion: EDOCallback?) {
        UIAnimator.activeAnimator = this
        var completed = false
        this.animationCreater = animationCreater@{
            val animator = ValueAnimator()
            animator.duration = (duration * 1000).toLong()
            animator.interpolator = LinearInterpolator()
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if (!completed) {
                        completed = true
                        completion?.invoke()
                    }
                }
                override fun onAnimationStart(animation: Animator?) { }
                override fun onAnimationCancel(animation: Animator?) { }
            })
            return@animationCreater LinearAnimation(animator)
        }
        animations.invoke()
        UIAnimator.activeAnimator = null
    }

    fun spring(tension: Double, friction: Double, animations: EDOCallback, completion: EDOCallback?) {
        UIAnimator.activeAnimator = this
        var completed = false
        this.animationCreater = animationCreater@{
            val spring = this.springSystem.createSpring()
            spring.springConfig = SpringConfig(tension, friction)
            spring.addListener(object : SpringListener {
                override fun onSpringUpdate(spring: Spring?) { }
                override fun onSpringEndStateChange(spring: Spring?) { }
                override fun onSpringAtRest(spring: Spring?) {
                    if (!completed) {
                        completed = true
                        completion?.invoke()
                    }
                }
                override fun onSpringActivate(spring: Spring?) { }
            })
            return@animationCreater SpringAnimation(spring)
        }
        animations.invoke()
        UIAnimator.activeAnimator = null
    }

    companion object {

        @JvmStatic val shared = UIAnimator()

        var activeAnimator: UIAnimator? = null
            private set

        internal var duringAnimationValueSet = false

    }

}

fun KIMIPackage.installUIAnimator() {
    exporter.exportClass(UIAnimator::class.java, "UIAnimator")
    exporter.exportInitializer(UIAnimator::class.java, {
        return@exportInitializer UIAnimator.shared
    })
    exporter.exportMethodToJavaScript(UIAnimator::class.java, "linear")
    exporter.exportMethodToJavaScript(UIAnimator::class.java, "spring")
    exporter.exportStaticProperty(UIAnimator::class.java, "shared")
    exporter.exportScript(UIAnimator::class.java, "UIAnimator.linear = function(){ UIAnimator.shared.linear.apply(UIAnimator.shared, arguments) }", false)
    exporter.exportScript(UIAnimator::class.java, "UIAnimator.spring = function(){ UIAnimator.shared.spring.apply(UIAnimator.shared, arguments) }", false)
}