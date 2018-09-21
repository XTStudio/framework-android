package com.xt.kimi.uikit

import android.app.Activity
import com.xt.endo.CGRect
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage
import kotlin.math.max

open class UITabBarController: UIViewController() {

    internal var itemControllers: List<UIViewController> = listOf()

    var selectedIndex: Int = -1
        set(value) {
            if (field == value) {
                EDOJavaHelper.emit(this, "onSelectedViewController", this, true)
                return
            }
            if (value < 0) {
                field = value
                return
            }
            val oldIndex = field
            this.itemControllers.getOrNull(value)?.let {
                if (it.parentViewController == null) {
                    this.addChildViewController(it)
                    this.view.addSubview(it.view)
                    this.view.bringSubviewToFront(this.tabBar)
                    this.viewWillLayoutSubviews()
                }
            }
            this.itemControllers.getOrNull(oldIndex)?.viewWillDisappear(false)
            this.itemControllers.getOrNull(value)?.viewWillAppear(false)
            field = value
            EDOJavaHelper.valueChanged(this, "selectedIndex")
            EDOJavaHelper.valueChanged(this, "selectedViewController")
            this.childViewControllers.forEach {
                it.view.hidden = itemControllers.indexOf(it) != value
            }
            this.tabBar.setSelectedIndex(value)
            this.setNeedsStatusBarAppearanceUpdate()
            this.itemControllers.getOrNull(oldIndex)?.viewDidDisappear(false)
            this.itemControllers.getOrNull(value)?.viewDidAppear(false)
            EDOJavaHelper.emit(this, "onSelectedViewController", this, false)
        }

    var selectedViewController: UIViewController?
        set(value) {
            this.selectedIndex = max(0, this.itemControllers.indexOf(value))
        }
        get() {
            return this.itemControllers.getOrNull(this.selectedIndex)
        }

    fun edo_setViewControllers(viewControllers: List<UIViewController>, animated: Boolean? = false) {
        this.childViewControllers.forEach {
            it.removeFromParentViewController()
            it.view.removeFromSuperview()
        }
        this.itemControllers = viewControllers
        viewControllers.forEachIndexed { index, it ->
            if (index == 0) {
                this.addChildViewController(it)
                this.view.addSubview(it.view)
            }
        }
        this.view.bringSubviewToFront(this.tabBar)
        this.tabBar.resetItems()
        this.selectedIndex = 0
        this.viewWillLayoutSubviews()
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

    private val navigationControllerFrame: CGRect
        get() {
            return CGRect(0.0, 0.0, this.view.bounds.width, this.view.bounds.height)
        }

    private val hidesBottomBarContentFrame: CGRect
        get() {
            return CGRect(0.0, 0.0, this.view.bounds.width, this.view.bounds.height)
        }

    init {
        this.tabBar.isImportantNodeForRendering = true
    }

    override fun viewDidLoad() {
        this.tabBar.tabBarController = this
        this.view.addSubview(this.tabBar)
        super.viewDidLoad()
    }

    override fun viewWillLayoutSubviews() {
        this.tabBar.frame = barFrame
        this.childViewControllers.forEach {
            if (it is UINavigationController) {
                it.view.frame = navigationControllerFrame
            }
            else {
                it.view.frame = contentFrame
            }
        }
        super.viewWillLayoutSubviews()
    }

    override fun setNeedsStatusBarAppearanceUpdate(activity: Activity?) {
        super.setNeedsStatusBarAppearanceUpdate(activity)
        this.selectedViewController?.setNeedsStatusBarAppearanceUpdate(activity)
    }

}

fun KIMIPackage.installUITabBarController() {
    exporter.exportClass(UITabBarController::class.java, "UITabBarController", "UIViewController")
    exporter.exportProperty(UITabBarController::class.java, "selectedIndex", false, true, true)
    exporter.exportProperty(UITabBarController::class.java, "selectedViewController", false, true, true)
    exporter.exportMethodToJavaScript(UITabBarController::class.java, "edo_setViewControllers")
    exporter.exportProperty(UITabBarController::class.java, "tabBar", true, true)
}