package com.xt.kimi.uikit

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.xt.endo.CGRect
import com.xt.endo.EDOCallback
import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage
import com.xt.kimi.currentActivity

enum class UIStatusBarStyle {
    default,
    lightContent,
}

open class UIViewController {

    var title: String? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "title")
            this.navigationItem.viewController = this
            this.navigationItem.setNeedsUpdate()
        }

    private var _view: UIView? = null
        get() {
            if (field == null) {
                loadView()
                field?.viewDelegate = this
                this.viewDidLoad()
            }
            return field
        }
        set(value) {
            if (field != null) { return }
            field = value
        }

    var view: UIView
        protected set(value) {
            this._view = value
        }
        get() {
            return this._view!!
        }

    var safeAreaInsets: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            if (field.top == value.top && field.left == value.left && field.bottom == value.bottom && field.right == value.right) {
                return
            }
            field = value
            EDOJavaHelper.valueChanged(this, "safeAreaInsets")
            this.view.setNeedsLayout(true)
        }

    fun attachToActivity(activity: Activity, statusBarTransparent: Boolean, isKeyWindow: Boolean = true) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        val rootView = UIWindow()
        if (statusBarTransparent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.window.statusBarColor = Color.TRANSPARENT
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                rootView.transparentStatusBar = true
                rootView.statusBarHeight = this.getStatusBarHeight(activity)
                rootView.softButtonBarHeight = this.getSoftButtonsBarHeight(activity)
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                rootView.transparentStatusBar = true
                rootView.statusBarHeight = this.getStatusBarHeight(activity)
            }
        }
        rootView.rootViewController = this
        if (isKeyWindow) {
            rootView.setBackgroundColor(Color.WHITE)
            activity.setContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            this.setNeedsStatusBarAppearanceUpdate(activity)
        }
        else {
            activity.addContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    fun detachFromActivity() {
        this.view.window?.removeFromSuperview()
    }

    private fun getStatusBarHeight(activity: Activity): Double {
        var result = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return (result / scale).toDouble()
    }

    private fun getSoftButtonsBarHeight(activity: Activity): Double {
        if ((activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) == 0) {
            return 0.0
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val metrics = DisplayMetrics()
            activity?.let {
                if (it.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    it.windowManager.defaultDisplay.getMetrics(metrics)
                    val usableHeight = metrics.heightPixels
                    it.windowManager.defaultDisplay.getRealMetrics(metrics)
                    val realHeight = metrics.heightPixels
                    return if (realHeight > usableHeight)
                        ((realHeight - usableHeight) / activity.resources.displayMetrics.density).toDouble()
                    else
                        0.0
                }
                else if (it.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    it.windowManager.defaultDisplay.getMetrics(metrics)
                    val usableHeight = metrics.widthPixels
                    it.windowManager.defaultDisplay.getRealMetrics(metrics)
                    val realHeight = metrics.widthPixels
                    return if (realHeight > usableHeight)
                        ((realHeight - usableHeight) / activity.resources.displayMetrics.density).toDouble()
                    else
                        0.0
                }
            }
        }
        return 0.0
    }

    protected open fun loadView() {
        this.view = UIView()
        this.view.edo_backgroundColor = UIColor.white
    }

    open fun viewDidLoad() {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidLoad")
    }

    open fun viewWillAppear(animated: Boolean) {
        this.setNeedsStatusBarAppearanceUpdate()
        EDOJavaHelper.invokeBindedMethod(this, "viewWillAppear", animated)
        (this as? UITabBarController)?.let {
            it.selectedViewController?.viewWillAppear(animated)
        } ?: kotlin.run {
            (this as? UINavigationController)?.let {
                it.childViewControllers.lastOrNull()?.viewWillAppear(animated)
            }
        } ?: kotlin.run {
            this.childViewControllers.forEach { it.viewWillAppear(animated) }
        }
    }

    open fun viewDidAppear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidAppear", animated)
        (this as? UITabBarController)?.let {
            it.selectedViewController?.viewDidAppear(animated)
        } ?: kotlin.run {
            (this as? UINavigationController)?.let {
                it.childViewControllers.lastOrNull()?.viewDidAppear(animated)
            }
        } ?: kotlin.run {
            this.childViewControllers.forEach { it.viewDidAppear(animated) }
        }
    }

    open fun viewWillDisappear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewWillDisappear", animated)
        this.window?.endEditing()
        (this as? UITabBarController)?.let {
            it.selectedViewController?.viewWillDisappear(animated)
        } ?: kotlin.run {
            (this as? UINavigationController)?.let {
                it.childViewControllers.lastOrNull()?.viewWillDisappear(animated)
            }
        } ?: kotlin.run {
            this.childViewControllers.forEach { it.viewWillDisappear(animated) }
        }
    }

    open fun viewDidDisappear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidDisappear", animated)
        (this as? UITabBarController)?.let {
            it.selectedViewController?.viewDidDisappear(animated)
        } ?: kotlin.run {
            (this as? UINavigationController)?.let {
                it.childViewControllers.lastOrNull()?.viewDidDisappear(animated)
            }
        } ?: kotlin.run {
            this.childViewControllers.forEach { it.viewDidDisappear(animated) }
        }
    }

    open fun viewWillLayoutSubviews() {
        EDOJavaHelper.invokeBindedMethod(this, "viewWillLayoutSubviews")
        EDOJavaHelper.emit(this, "viewWillLayoutSubviews", this)
    }

    open fun viewDidLayoutSubviews() {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidLayoutSubviews")
    }

    var parentViewController: UIViewController? = null
        private set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "parentViewController")
            EDOJavaHelper.valueChanged(this, "navigationController")
            EDOJavaHelper.valueChanged(this, "tabBarController")
        }

    var presentedViewController: UIViewController? = null
        internal set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "presentedViewController")
        }

    var presentingViewController: UIViewController? = null
        internal set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "presentingViewController")
        }

    open fun presentViewController(viewController: UIViewController, animated: Boolean? = true, completion: EDOCallback? = null) {
        val window = this.window ?: return
        val visibleViewController = this.visibleViewController ?: return
        if (visibleViewController.presentedViewController != null || viewController.presentingViewController != null || viewController.parentViewController != null) {
            return
        }
        visibleViewController.viewWillDisappear(animated != false)
        viewController.viewWillAppear(animated != false)
        viewController.presentingViewController = visibleViewController
        visibleViewController.presentedViewController = viewController
        window.presentViewController(viewController, animated != false) {
            completion?.invoke()
            visibleViewController.viewDidDisappear(animated != false)
            viewController.viewDidAppear(animated != false)
        }
    }

    open fun dismissViewController(animated: Boolean? = true, completion: EDOCallback? = null) {
        val window = this.window ?: return
        window.dismissViewController(animated != false) {
            completion?.invoke()
        }
    }

    var childViewControllers: List<UIViewController> = listOf()
        private set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "childViewControllers")
        }

    open fun addChildViewController(viewController: UIViewController) {
        if (viewController == this) { return }
        viewController.parentViewController?.let {
            if (it == this) { return }
            viewController.removeFromParentViewController()
        }
        viewController.willMoveToParentViewController(this)
        this.childViewControllers = kotlin.run {
            val mutable = this.childViewControllers.toMutableList()
            mutable.add(viewController)
            return@run mutable
        }
        viewController.parentViewController = this
        viewController.didMoveToParentViewController(this)
    }

    open fun removeFromParentViewController() {
        this.parentViewController?.let {
            this.willMoveToParentViewController(null)
            it.childViewControllers = kotlin.run {
                val mutable = it.childViewControllers.toMutableList()
                mutable.remove(this)
                return@run mutable
            }
            this.parentViewController = null
            this.didMoveToParentViewController(null)
        }
    }

    open fun willMoveToParentViewController(parent: UIViewController?) {
        EDOJavaHelper.invokeBindedMethod(this, "willMoveToParentViewController", parent)
    }

    open fun didMoveToParentViewController(parent: UIViewController?) {
        EDOJavaHelper.invokeBindedMethod(this, "didMoveToParentViewController", parent)
    }

    internal open fun didAddSubview(subview: UIView) { }

    val navigationController: UINavigationController?
        get() {
            var current: UIViewController? = this
            while (current != null) {
                (current as? UINavigationController)?.let {
                    return it
                }
                current = current.parentViewController
            }
            return null
        }

    val navigationItem = UINavigationItem()

    var hidesBottomBarWhenPushed: Boolean = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "hidesBottomBarWhenPushed")
        }

    val tabBarController: UITabBarController?
        get() {
            var current: UIViewController? = this
            while (current != null) {
                (current as? UITabBarController)?.let {
                    return it
                }
                current = current.parentViewController
            }
            return null
        }

    val tabBarItem = UITabBarItem()

    internal var window: UIWindow? = null
        get() {
            return field ?: parentViewController?.window
        }

    internal val visibleViewController: UIViewController?
        get() {
            return window?.presentedViewControllers?.lastOrNull() ?: window?.rootViewController
        }

    // Device Back Button Support

    fun canGoBack(childBacking: Boolean = false): Boolean {
        if (UIActionSheet.currentActionSheet != null) {
            return true
        }
        if (!childBacking && this.window?.presentedViewControllers?.count() ?: 0 > 0) {
            return true
        }
        (this as? UINavigationController)?.let {
            return it.childViewControllers.count() > 1
        }
        (this as? UITabBarController)?.let {
            return it.selectedViewController?.canGoBack() ?: false
        }
        return false
    }

    fun goBack(childBacking: Boolean = false) {
        if (UIActionSheet.currentActionSheet != null) {
            UIActionSheet.currentActionSheet?.dismiss(true) { }
            return
        }
        if (!childBacking && this.window?.presentedViewControllers?.count() ?: 0 > 0) {
            this.window?.presentedViewControllers?.lastOrNull()?.takeIf { it.canGoBack(true) }?.let {
                return it.goBack(true)
            }
            this.window?.dismissViewController(true)
            return
        }
        (this as? UINavigationController)?.let {
            it.popViewController(true)
        }
        (this as? UITabBarController)?.let {
            it.selectedViewController?.goBack()
        }
    }

    // Keyboard support.

    internal fun keyboardWillShow(keyboardHeight: Double) {
        EDOJavaHelper.emit(this, "keyboardWillShow", CGRect(0.0, 0.0, this.view.bounds.width, keyboardHeight), 0.0)
        this.childViewControllers.forEach { it.keyboardWillShow(keyboardHeight) }
    }

    internal fun keyboardWillHide() {
        EDOJavaHelper.emit(this, "keyboardWillHide", 0.0)
        this.childViewControllers.forEach { it.keyboardWillHide() }
    }

    // StatusBar support.

    open fun setNeedsStatusBarAppearanceUpdate(activity: Activity? = null) {
        (EDOJavaHelper.value(this, "statusBarStyle") as? UIStatusBarStyle)?.let {
            val activity = activity ?: currentActivity ?: return@let
            when (it) {
                UIStatusBarStyle.default -> {
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                UIStatusBarStyle.lightContent -> {
                    activity.window.decorView.systemUiVisibility = 0
                }
            }
        }
    }

}

fun KIMIPackage.installUIViewController() {
    exporter.exportClass(UIViewController::class.java, "UIViewController")
    exporter.exportProperty(UIViewController::class.java, "title", false, true, true)
    exporter.exportProperty(UIViewController::class.java, "view", true, true)
    exporter.exportProperty(UIViewController::class.java, "safeAreaInsets", true, true)
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidLoad")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillAppear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidAppear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillDisappear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidDisappear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillLayoutSubviews")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidLayoutSubviews")
    exporter.exportProperty(UIViewController::class.java, "parentViewController", true, true)
    exporter.exportProperty(UIViewController::class.java, "presentedViewController", true, true)
    exporter.exportProperty(UIViewController::class.java, "presentingViewController", true, true)
    exporter.exportMethodToJavaScript(UIViewController::class.java, "presentViewController")
    exporter.exportMethodToJavaScript(UIViewController::class.java, "dismissViewController")
    exporter.exportProperty(UIViewController::class.java, "childViewControllers", true, true)
    exporter.exportMethodToJavaScript(UIViewController::class.java, "addChildViewController")
    exporter.exportMethodToJavaScript(UIViewController::class.java, "removeFromParentViewController")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "willMoveToParentViewController")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "didMoveToParentViewController")
    exporter.exportProperty(UIViewController::class.java, "navigationController", true, true)
    exporter.exportProperty(UIViewController::class.java, "navigationItem", true, true)
    exporter.exportProperty(UIViewController::class.java, "tabBarController", true, true)
    exporter.exportProperty(UIViewController::class.java, "tabBarItem", true, true)
    exporter.exportMethodToJavaScript(UIViewController::class.java, "setNeedsStatusBarAppearanceUpdate")
    exporter.exportEnum("UIStatusBarStyle", mapOf(
            Pair("default", UIStatusBarStyle.default),
            Pair("lightContent", UIStatusBarStyle.lightContent)
    ))
}