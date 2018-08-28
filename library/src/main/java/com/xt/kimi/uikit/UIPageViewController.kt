package com.xt.kimi.uikit

import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import kotlin.math.abs
import kotlin.math.ceil

class UIPageViewController(val isVertical: Boolean? = false): UIViewController() {

    var loops: Boolean = false

    var pageItems: List<UIViewController>? = null
        set(value) {
            field = value
            field?.takeIf { it.count() > 0 }?.let {
                this.currentPage = it.firstOrNull()
                this.resetContents()
            }
        }

    var currentPage: UIViewController? = null
        set(value) {
            field?.let { it.removeFromParentViewController() }
            field = value
            value?.let {
                if (it.parentViewController != this) {
                    this.addChildViewController(it)
                }
            }
            this.resetContents()
        }

    fun scrollToNextPage(animated: Boolean? = true) {
        if (this.isVertical == true) {
            if (this.scrollView.contentInset.bottom > 0.0) {
                this.scrollView.setContentOffset(CGPoint(0.0, this.scrollView.contentInset.bottom), animated ?: true)
                if (animated == false) {
                    this.changeContents()
                }
            }
        }
        else {
            if (this.scrollView.contentInset.right > 0.0) {
                this.scrollView.setContentOffset(CGPoint(this.scrollView.contentInset.right, 0.0), animated ?: true)
                if (animated == false) {
                    this.changeContents()
                }
            }
        }
    }

    fun scrollToPreviousPage(animated: Boolean? = true) {
        if (this.isVertical == true) {
            if (this.scrollView.contentInset.top > 0.0) {
                this.scrollView.setContentOffset(CGPoint(0.0, -this.scrollView.contentInset.top), animated ?: true)
                if (animated == false) {
                    this.changeContents()
                }
            }
        }
        else {
            if (this.scrollView.contentInset.left > 0.0) {
                this.scrollView.setContentOffset(CGPoint(-this.scrollView.contentInset.left, 0.0), animated ?: true)
                if (animated == false) {
                    this.changeContents()
                }
            }
        }
    }

    protected open fun beforeViewController(currentPage: UIViewController): UIViewController? {
        pageItems?.let { pageItems ->
            val currentIndex = pageItems.indexOf(currentPage).takeIf { it >= 0 } ?: return null
            if (currentIndex > 0) {
                return pageItems[currentIndex - 1]
            }
            else if (this.loops && pageItems.count() > 1) {
                return pageItems.lastOrNull()
            }
        }
        (EDOJavaHelper.value(this, "beforeViewController", currentPage) as? UIViewController)?.let {
            return it
        }
        return null
    }

    protected open fun afterViewController(currentPage: UIViewController): UIViewController? {
        pageItems?.let { pageItems ->
            val currentIndex = pageItems.indexOf(currentPage).takeIf { it >= 0 } ?: return null
            if (currentIndex + 1 < pageItems.count()) {
                return pageItems[currentIndex + 1]
            }
            else if (this.loops && pageItems.count() > 1) {
                return pageItems.firstOrNull()
            }
        }
        (EDOJavaHelper.value(this, "afterViewController", currentPage) as? UIViewController)?.let {
            return it
        }
        return null
    }

    protected open fun didFinishAnimating(currentPage: UIViewController, previousPage: UIViewController) {
        EDOJavaHelper.emit(this, "didFinishAnimating", currentPage, previousPage)
    }

    // Implementation

    private val scrollView = object : UIScrollView() {

        override fun didEndDecelerating() {
            super.didEndDecelerating()
            this@UIPageViewController.changeContents()
        }

        override fun didEndScrollingAnimation() {
            super.didEndScrollingAnimation()
            this@UIPageViewController.changeContents()
        }

    }

    override fun viewDidLoad() {
        this.scrollView.pagingEnabled = true
        this.scrollView.bounces = false
        this.scrollView.showsHorizontalScrollIndicator = false
        this.scrollView.showsVerticalScrollIndicator = false
        this.view.addSubview(this.scrollView)
        super.viewDidLoad()
    }

    override fun viewWillLayoutSubviews() {
        this.scrollView.frame = this.view.bounds
        this.scrollView.contentSize = CGSize(this.view.bounds.width, this.view.bounds.height)
        this.resetContents()
        super.viewWillLayoutSubviews()
    }

    private fun changeContents() {
        if (this.isVertical == true) {
            if (abs(this.scrollView.edo_contentOffset.y - (-this.scrollView.bounds.height)) < 4.0) {
                val currentPage = this.currentPage ?: return
                val beforePage = this.beforeViewController(currentPage) ?: return
                this.currentPage = beforePage
                this.resetContents()
                this.didFinishAnimating(beforePage, currentPage)
            }
            else if (abs(this.scrollView.edo_contentOffset.y - this.scrollView.bounds.height) < 4.0) {
                val currentPage = this.currentPage ?: return
                val afterPage = this.afterViewController(currentPage) ?: return
                this.currentPage = afterPage
                this.resetContents()
                this.didFinishAnimating(afterPage, currentPage)
            }
        }
        else {
            if (abs(this.scrollView.edo_contentOffset.x - (-this.scrollView.bounds.width)) < 4.0) {
                val currentPage = this.currentPage ?: return
                val beforePage = this.beforeViewController(currentPage) ?: return
                this.currentPage = beforePage
                this.resetContents()
                this.didFinishAnimating(beforePage, currentPage)
            }
            else if (abs(this.scrollView.edo_contentOffset.x - this.scrollView.bounds.width) < 4.0) {
                val currentPage = this.currentPage ?: return
                val afterPage = this.afterViewController(currentPage) ?: return
                this.currentPage = afterPage
                this.resetContents()
                this.didFinishAnimating(afterPage, currentPage)
            }
        }
    }

    private fun resetContents() {
        val currentPage = this.currentPage ?: return
        val beforePage = this.beforeViewController(currentPage)
        val afterPage = this.afterViewController(currentPage)
        this.scrollView.edo_subviews.forEach {
            if (it != currentPage.view && it != beforePage?.view && it != afterPage?.view) {
                it.removeFromSuperview()
            }
        }
        currentPage.view.frame = this.view.bounds
        this.scrollView.addSubview(currentPage.view)
        beforePage?.let {
            this.scrollView.addSubview(it.view)
            if (this.isVertical == true) {
                it.view.frame = CGRect(0.0, -this.view.bounds.height, this.view.bounds.width, this.view.bounds.height)
            }
            else {
                it.view.frame = CGRect(-this.view.bounds.width, 0.0, this.view.bounds.width, this.view.bounds.height)
            }
        }
        afterPage?.let {
            this.scrollView.addSubview(it.view)
            if (this.isVertical == true) {
                it.view.frame = CGRect(0.0, this.view.bounds.height, this.view.bounds.width, this.view.bounds.height)
            }
            else {
                it.view.frame = CGRect(this.view.bounds.width, 0.0, this.view.bounds.width, this.view.bounds.height)
            }
        }
        if (this.isVertical == true) {
            this.scrollView.contentInset = UIEdgeInsets(
                    if (beforePage != null) ceil(this.view.bounds.height) else 0.0,
                    0.0,
                    if (afterPage != null) ceil(this.view.bounds.height) else 0.0,
                    0.0
            )
        }
        else {
            this.scrollView.contentInset = UIEdgeInsets(
                    0.0,
                    if (beforePage != null) ceil(this.view.bounds.width) else 0.0,
                    0.0,
                    if (afterPage != null) ceil(this.view.bounds.width) else 0.0
            )
        }
        this.scrollView.edo_contentOffset = CGPoint(0.0, 0.0)
    }

}

fun KIMIPackage.installUIPageViewController() {
    exporter.exportClass(UIPageViewController::class.java, "UIPageViewController", "UIViewController")
    exporter.exportInitializer(UIPageViewController::class.java) {
        return@exportInitializer UIPageViewController(it.firstOrNull() as? Boolean ?: false)
    }
    exporter.exportProperty(UIPageViewController::class.java, "loops")
    exporter.exportProperty(UIPageViewController::class.java, "pageItems")
    exporter.exportProperty(UIPageViewController::class.java, "currentPage")
    exporter.exportMethodToJavaScript(UIPageViewController::class.java, "scrollToNextPage")
    exporter.exportMethodToJavaScript(UIPageViewController::class.java, "scrollToPreviousPage")
}