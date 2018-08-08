package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

open class UINavigationController(rootViewController: UIViewController? = null): UIViewController() {

    val navigationBar = UINavigationBar()

    init {
        rootViewController?.let {
            this.pushViewController(it, false)
        }
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        this.view.addSubview(this.navigationBar)
    }

    fun canGoBack(): Boolean {
        return this.childViewControllers.count() > 1
    }

    fun goBack() {
        this.popViewController(true)
    }

    fun pushViewController(viewController: UIViewController, animated: Boolean? = true) {
        this.addChildViewController(viewController)
        this.view.addSubview(viewController.view)
        viewController.view.frame = contentFrame
        val fromViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 2)
        val toViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 1)
        if (animated != false && fromViewController != null && toViewController != null) {
            fromViewController.viewWillDisappear(true)
            toViewController.viewWillAppear(true)
            this.doPushAnimation(fromViewController, toViewController) {
                fromViewController.viewDidDisappear(true)
                toViewController.viewDidAppear(true)
            }
            this.navigationBar.pushNavigationItem(toViewController.navigationItem, true)
        }
        else {
            fromViewController?.viewWillDisappear(false)
            toViewController?.viewWillAppear(false)
            fromViewController?.viewDidDisappear(false)
            toViewController?.viewDidAppear(false)
            toViewController?.navigationItem?.let {
                this.navigationBar.pushNavigationItem(it, false)
            }
        }
    }

    fun popViewController(animated: Boolean? = true): UIViewController? {
        val fromViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 1) ?: return null
        val toViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 2) ?: return null
        fromViewController.removeFromParentViewController()
        if (animated != false) {
            this.doPopAnimation(fromViewController, toViewController) {
                fromViewController.view.removeFromSuperview()
            }
        }
        else {
            fromViewController.view.removeFromSuperview()
            toViewController.view.frame = contentFrame
        }
        this.navigationBar.popNavigationItem(animated != false)
        return fromViewController
    }

    fun popToViewController(viewController: UIViewController, animated: Boolean? = true): List<UIViewController> {
        if (!this.childViewControllers.contains(viewController)) { return emptyList() }
        val targetIndex = this.childViewControllers.indexOf(viewController)
        val fromViewControllers = this.childViewControllers.filterIndexed { index, _ -> index > targetIndex }
        if (fromViewControllers.count() == 0) {
            return emptyList()
        }
        val toViewController = viewController
        fromViewControllers.forEach { it.removeFromParentViewController() }
        if (animated != false) {
            this.doPopAnimation(fromViewControllers.last(), toViewController) {
                fromViewControllers.forEach { it.view.removeFromSuperview() }
            }
        }
        else {
            fromViewControllers.forEach { it.view.removeFromSuperview() }
            toViewController.view.frame = contentFrame
        }
        this.navigationBar.popToNavigationItem(toViewController.navigationItem, animated != false)
        return fromViewControllers
    }

    fun popToRootViewController(animated: Boolean? = true): List<UIViewController> {
        val rootViewController = this.childViewControllers.firstOrNull() ?: return emptyList()
        return this.popToViewController(rootViewController, animated)
    }

    fun edo_setViewControllers(viewControllers: List<UIViewController>, animated: Boolean? = false) {
        this.childViewControllers.forEach {
            it.removeFromParentViewController()
            it.view.removeFromSuperview()
        }
        viewControllers.forEach {
            this.addChildViewController(it)
            this.view.addSubview(it.view)
            it.view.frame = contentFrame
        }
    }

    override fun viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.navigationBar.frame = barFrame
        this.childViewControllers.forEach {
            it.view.frame = contentFrame
        }
    }

    private val barFrame: CGRect
        get() {
            return CGRect(0.0, 0.0, this.view.bounds.width, (this.window?.statusBarHeight ?: 0.0) + this.navigationBar.barHeight)
        }

    private val contentFrame: CGRect
        get() {
            return CGRect(0.0, this.barFrame.height, this.view.bounds.width, this.view.bounds.height - this.barFrame.height)
        }

    private fun doPushAnimation(fromViewController: UIViewController, toViewController: UIViewController, complete: () -> Unit) {
        fromViewController.view.frame = contentFrame
        toViewController.view.frame = CGRect(contentFrame.width, contentFrame.y, contentFrame.width, contentFrame.height)
        UIAnimator.shared.curve(0.25, EDOCallback.createWithBlock {
            fromViewController.view.frame = CGRect(-contentFrame.width * 0.25, contentFrame.y, contentFrame.width, contentFrame.height)
            toViewController.view.frame = contentFrame
        }, EDOCallback.createWithBlock {
            complete()
        })
    }

    private fun doPopAnimation(fromViewController: UIViewController, toViewController: UIViewController, complete: () -> Unit) {
        fromViewController.view.frame = contentFrame
        toViewController.view.frame = CGRect(-contentFrame.width * 0.25, contentFrame.y, contentFrame.width, contentFrame.height)
        UIAnimator.shared.curve(0.25, EDOCallback.createWithBlock {
            fromViewController.view.frame = CGRect(contentFrame.width, contentFrame.y, contentFrame.width, contentFrame.height)
            toViewController.view.frame = contentFrame
        }, EDOCallback.createWithBlock {
            complete()
        })
    }

    override fun didAddSubview(subview: UIView) {
        super.didAddSubview(subview)
        this.view.bringSubviewToFront(this.navigationBar)
    }

}

fun KIMIPackage.installUINavigationController() {
    exporter.exportClass(UINavigationController::class.java, "UINavigationController", "UIViewController")
    exporter.exportInitializer(UINavigationController::class.java) {
        return@exportInitializer UINavigationController(it.firstOrNull() as? UIViewController)
    }
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "pushViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popToViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popToRootViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "edo_setViewControllers")
}