package com.xt.kimi.uikit

import android.app.Activity
import com.xt.endo.CGRect
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage

open class UINavigationController(private val rootViewController: UIViewController? = null): UIViewController() {

    val navigationBar = UINavigationBar()

    private var beingAnimating = false

    override fun viewDidLoad() {
        this.navigationBar.navigationController = this
        this.view.edo_backgroundColor = UIColor.clear
        this.view.addSubview(this.navigationBar)
        this.rootViewController?.let {
            this.pushViewController(it, false)
        }
        super.viewDidLoad()
    }

    fun pushViewController(viewController: UIViewController, animated: Boolean? = true) {
        if (this.beingAnimating) { return }
        if (this.childViewControllers.count() > 0) {
            viewController.hidesBottomBarWhenPushed = true
        }
        if (viewController.hidesBottomBarWhenPushed) {
            this.tabBarController?.view?.bringSubviewToFront(this.view)
        }
        this.addChildViewController(viewController)
        this.view.addSubview(viewController.view)
        viewController.view.frame = contentFrame(viewController)
        val fromViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 2)
        val toViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 1)
        if (animated != false && fromViewController != null && toViewController != null) {
            fromViewController.viewWillDisappear(true)
            toViewController.viewWillAppear(true)
            this.beingAnimating = true
            this.doPushAnimation(fromViewController, toViewController) {
                fromViewController.viewDidDisappear(true)
                toViewController.viewDidAppear(true)
                this.childViewControllers.forEach {
                    if (it == toViewController) { return@forEach }
                    it.view.hidden = true
                }
                this.beingAnimating = false
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
            this.childViewControllers.forEach {
                if (it == toViewController) { return@forEach }
                it.view.hidden = true
            }
        }
    }

    fun popViewController(animated: Boolean? = true): UIViewController? {
        if (this.beingAnimating) { return null }
        val fromViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 1) ?: return null
        val toViewController = this.childViewControllers.getOrNull(this.childViewControllers.count() - 2) ?: return null
        toViewController.view.hidden = false
        fromViewController.viewWillDisappear(animated ?: true)
        toViewController.viewWillAppear(animated ?: true)
        if (animated != false) {
            this.beingAnimating = true
            this.doPopAnimation(fromViewController, toViewController) {
                fromViewController.removeFromParentViewController()
                fromViewController.view.removeFromSuperview()
                fromViewController.viewDidDisappear(true)
                toViewController.viewDidAppear(true)
                if (!toViewController.hidesBottomBarWhenPushed) {
                    this.tabBarController?.let {
                        it.view.bringSubviewToFront(it.tabBar)
                    }
                }
                this.beingAnimating = false
            }
        }
        else {
            fromViewController.removeFromParentViewController()
            fromViewController.view.removeFromSuperview()
            toViewController.view.frame = contentFrame(toViewController)
            fromViewController.viewDidDisappear(true)
            toViewController.viewDidAppear(true)
            if (!toViewController.hidesBottomBarWhenPushed) {
                this.tabBarController?.let {
                    it.view.bringSubviewToFront(it.tabBar)
                }
            }
        }
        this.navigationBar.popNavigationItem(animated != false)
        return fromViewController
    }

    fun popToViewController(viewController: UIViewController, animated: Boolean? = true): List<UIViewController> {
        if (this.beingAnimating) { return emptyList() }
        if (!this.childViewControllers.contains(viewController)) { return emptyList() }
        val targetIndex = this.childViewControllers.indexOf(viewController)
        val fromViewControllers = this.childViewControllers.filterIndexed { index, _ -> index > targetIndex }
        if (fromViewControllers.count() == 0) {
            return emptyList()
        }
        val toViewController = viewController
        toViewController.view.hidden = false
        fromViewControllers.forEach { it.viewWillDisappear(animated ?: true) }
        toViewController.viewWillAppear(animated ?: true)
        if (animated != false) {
            this.beingAnimating = true
            this.doPopAnimation(fromViewControllers.last(), toViewController) {
                fromViewControllers.forEach { it.removeFromParentViewController() }
                fromViewControllers.forEach { it.view.removeFromSuperview() }
                fromViewControllers.forEach { it.viewDidDisappear(true) }
                toViewController.viewDidAppear(true)
                if (!toViewController.hidesBottomBarWhenPushed) {
                    this.tabBarController?.let {
                        it.view.bringSubviewToFront(it.tabBar)
                    }
                }
                this.beingAnimating = false
            }
        }
        else {
            fromViewControllers.forEach { it.removeFromParentViewController() }
            fromViewControllers.forEach { it.view.removeFromSuperview() }
            toViewController.view.frame = contentFrame(toViewController)
            fromViewControllers.forEach { it.viewDidDisappear(false) }
            toViewController.viewDidAppear(false)
            if (!toViewController.hidesBottomBarWhenPushed) {
                this.tabBarController?.let {
                    it.view.bringSubviewToFront(it.tabBar)
                }
            }
        }
        this.navigationBar.popToNavigationItem(toViewController.navigationItem, animated != false)
        return fromViewControllers
    }

    fun popToRootViewController(animated: Boolean? = true): List<UIViewController> {
        val rootViewController = this.childViewControllers.firstOrNull() ?: return emptyList()
        return this.popToViewController(rootViewController, animated)
    }

    fun edo_setViewControllers(viewControllers: List<UIViewController>, animated: Boolean? = false) {
        if (this.beingAnimating) { return }
        this.childViewControllers.forEach {
            it.removeFromParentViewController()
            it.view.removeFromSuperview()
        }
        viewControllers.forEach {
            this.addChildViewController(it)
            this.view.addSubview(it.view)
            it.view.frame = contentFrame(it)
        }
        this.navigationBar.setItems(viewControllers.map { it.navigationItem }, animated != false)
        if (viewControllers.lastOrNull()?.hidesBottomBarWhenPushed == true) {
            this.tabBarController?.let {
                it.view.bringSubviewToFront(this.view)
            }
        }
        else {
            this.tabBarController?.let {
                it.view.bringSubviewToFront(it.tabBar)
            }
        }
    }

    override fun viewWillLayoutSubviews() {
        this.navigationBar.frame = barFrame
        this.childViewControllers.forEach {
            it.view.frame = contentFrame(it)
        }
        super.viewWillLayoutSubviews()
    }

    private val barFrame: CGRect
        get() {
            if (this.navigationBar.hidden) {
                return CGRect(0.0, 0.0, this.view.bounds.width, 0.0)
            }
            return CGRect(0.0, 0.0, this.view.bounds.width, (this.window?.statusBarHeight ?: 0.0) + this.navigationBar.barHeight)
        }

    private fun contentFrame(viewController: UIViewController): CGRect {
        if (viewController.hidesBottomBarWhenPushed) {
            return CGRect(0.0, this.barFrame.height, this.view.bounds.width, this.view.bounds.height - this.barFrame.height)
        }
        else {
            return CGRect(0.0, this.barFrame.height, this.view.bounds.width, this.view.bounds.height - this.barFrame.height - (this.tabBarController?.tabBar?.barHeight ?: 0.0))
        }
    }

    private fun doPushAnimation(fromViewController: UIViewController, toViewController: UIViewController, complete: () -> Unit) {
        fromViewController.view.frame = contentFrame(fromViewController)
        contentFrame(toViewController).let { contentFrame ->
            toViewController.view.frame = CGRect(contentFrame.width, contentFrame.y, contentFrame.width, contentFrame.height)
        }
        UIAnimator.shared.bouncy(0.0, 16.0, EDOCallback.createWithBlock {
            contentFrame(fromViewController).let { contentFrame ->
                fromViewController.view.frame = CGRect(-contentFrame.width * 0.25, contentFrame.y, contentFrame.width, contentFrame.height)
            }
            toViewController.view.frame = contentFrame(toViewController)
        }, EDOCallback.createWithBlock {
            complete()
        })
    }

    private fun doPopAnimation(fromViewController: UIViewController, toViewController: UIViewController, complete: () -> Unit) {
        if (fromViewController is UINavigationBarViewController) {
            fromViewController.view.frame = this.view.bounds
        }
        else {
            fromViewController.view.frame = contentFrame(fromViewController)
        }
        contentFrame(toViewController).let { contentFrame ->
            toViewController.view.frame = CGRect(-contentFrame.width * 0.25, contentFrame.y, contentFrame.width, contentFrame.height)
        }
        UIAnimator.shared.bouncy(0.0, 16.0, EDOCallback.createWithBlock {
            if (fromViewController is UINavigationBarViewController) {
                fromViewController.view.frame = CGRect(contentFrame(fromViewController).width, this.view.frame.y, this.view.frame.width, this.view.frame.height)
            }
            else {
                contentFrame(fromViewController).let { contentFrame ->
                    fromViewController.view.frame = CGRect(contentFrame.width, contentFrame.y, contentFrame.width, contentFrame.height)
                }
            }
            toViewController.view.frame = contentFrame(toViewController)
        }, EDOCallback.createWithBlock {
            complete()
        })
    }

    override fun didAddSubview(subview: UIView) {
        this.view.bringSubviewToFront(this.navigationBar)
        super.didAddSubview(subview)
    }

    fun setNavigationBarHidden(hidden: Boolean, animated: Boolean, fadeAnimation: Boolean = false) {
        if (animated) {
            if (fadeAnimation) {
                this.navigationBar.hidden = hidden
                this.navigationBar.edo_alpha = if (hidden) 1.0 else 0.0
            }
            UIAnimator.shared.curve(0.25, EDOCallback.createWithBlock {
                if (fadeAnimation) {
                    this.navigationBar.edo_alpha = if (hidden) 0.0 else 1.0
                }
                else {
                    this.navigationBar.hidden = hidden
                }
            }, null)
        }
        else {
            this.navigationBar.hidden = hidden
        }
        (this.childViewControllers.lastOrNull() as? UINavigationBarViewController)?.navigationControllerState?.barHidden = hidden
    }

    override fun setNeedsStatusBarAppearanceUpdate(activity: Activity?) {
        super.setNeedsStatusBarAppearanceUpdate(activity)
        this.childViewControllers.lastOrNull()?.setNeedsStatusBarAppearanceUpdate(activity)
    }

}

fun KIMIPackage.installUINavigationController() {
    exporter.exportClass(UINavigationController::class.java, "UINavigationController", "UIViewController")
    exporter.exportInitializer(UINavigationController::class.java) {
        return@exportInitializer UINavigationController(it.firstOrNull() as? UIViewController)
    }
    exporter.exportProperty(UINavigationController::class.java, "navigationBar", true)
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "setNavigationBarHidden")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "pushViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popToViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "popToRootViewController")
    exporter.exportMethodToJavaScript(UINavigationController::class.java, "edo_setViewControllers")
}