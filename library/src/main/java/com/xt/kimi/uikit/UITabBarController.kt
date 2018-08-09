package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.kimi.KIMIPackage
import kotlin.math.max

open class UITabBarController: UIViewController() {

    var selectedIndex: Int = 0
        set(value) {
            field = value
            this.childViewControllers.forEachIndexed { index, viewController ->
                viewController.view.hidden = index != value
            }
            this.tabBar.setSelectedIndex(value)
        }

    var selectedViewController: UIViewController?
        set(value) {
            this.selectedIndex = max(0, this.childViewControllers.indexOf(value))
        }
        get() {
            return this.childViewControllers.getOrNull(this.selectedIndex)
        }

    fun edo_setViewControllers(viewControllers: List<UIViewController>, animated: Boolean? = false) {
        this.childViewControllers.forEach {
            it.removeFromParentViewController()
            it.view.removeFromSuperview()
        }
        viewControllers.forEach {
            this.addChildViewController(it)
            this.view.addSubview(it.view)
        }
        this.view.bringSubviewToFront(this.tabBar)
        this.tabBar.resetItems()
        this.selectedIndex = 0
    }

    val tabBar: UITabBar = UITabBar()

    // Implementation

    private val barFrame: CGRect
        get() {
            if (this.tabBar.hidden) {
                return CGRect(0.0, this.view.bounds.height, this.view.bounds.width, 0.0)
            }
            return CGRect(0.0, this.view.bounds.height - this.tabBar.barHeight, this.view.bounds.width, this.tabBar.barHeight)
        }

    private val contentFrame: CGRect
        get() {
            return CGRect(0.0, 0.0, this.view.bounds.width, this.view.bounds.height - this.barFrame.height)
        }

    override fun viewDidLoad() {
        super.viewDidLoad()
        this.tabBar.tabBarController = this
        this.view.addSubview(this.tabBar)
    }

    override fun viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.tabBar.frame = barFrame
        this.childViewControllers.forEach {
            it.view.frame = contentFrame
        }
    }

}

fun KIMIPackage.installUITabBarController() {
    exporter.exportClass(UITabBarController::class.java, "UITabBarController", "UIViewController")
    exporter.exportProperty(UITabBarController::class.java, "selectedIndex")
    exporter.exportProperty(UITabBarController::class.java, "selectedViewController")
    exporter.exportMethodToJavaScript(UITabBarController::class.java, "edo_setViewControllers")
    exporter.exportProperty(UITabBarController::class.java, "tabBar", true)
}