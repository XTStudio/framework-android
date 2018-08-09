package com.xt.kimi.uikit

import com.xt.endo.CGPoint
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage

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

    fun scrollToNextPage(animated: Boolean? = true) {

    }

    fun scrollToPreviousPage(animated: Boolean? = true) {

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
        return null
    }

    protected open fun didFinishAnimating(currentPage: UIViewController, previousPages: List<UIViewController>): UIViewController? {
        return null
    }

    // Implementation

    private val scrollView = UIScrollView()

    override fun viewDidLoad() {
        super.viewDidLoad()
        this.scrollView.pagingEnabled = true
        this.scrollView.bounces = false
        this.scrollView.showsHorizontalScrollIndicator = false
        this.scrollView.showsVerticalScrollIndicator = false
        this.view.addSubview(this.scrollView)
    }

    override fun viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.scrollView.frame = this.view.bounds
        this.scrollView.contentSize = CGSize(this.view.bounds.width, this.view.bounds.height)
    }

    private fun resetContents() {
        val currentPage = this.currentPage ?: return
        val beforePage = this.beforeViewController(currentPage)
        val afterPage = this.afterViewController(currentPage)
        currentPage.view.frame = this.view.bounds
        beforePage?.let {
            this.scrollView.addSubview(it.view)
            it.view.frame = CGRect(-this.view.bounds.width, 0.0, this.view.bounds.width, this.view.bounds.height)
        }
        afterPage?.let {
            this.scrollView.addSubview(it.view)
            it.view.frame = CGRect(this.view.bounds.width, 0.0, this.view.bounds.width, this.view.bounds.height)
        }
        this.scrollView.contentInset = UIEdgeInsets(
                0.0,
                if (beforePage != null) this.view.bounds.width else 0.0,
                0.0,
                if (afterPage != null) this.view.bounds.width else 0.0
                )
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