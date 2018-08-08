package com.xt.kimi.uikit

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.xt.endo.EDOCallback
import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage

open class UIViewController {

    var title: String? = null
        set(value) {
            field = value
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
            this.view.setNeedsLayout(true)
        }

    fun attachToActivity(activity: Activity, statusBarTransparent: Boolean) {
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
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                rootView.transparentStatusBar = true
                rootView.statusBarHeight = this.getStatusBarHeight(activity)
            }
        }
        rootView.setBackgroundColor(Color.WHITE)
        rootView.rootViewController = this
        activity.setContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun getStatusBarHeight(activity: Activity): Double {
        var result = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return (result / scale).toDouble()
    }

    protected open fun loadView() {
        this.view = UIView()
        this.view.edo_backgroundColor = UIColor.white
    }

    open fun viewDidLoad() {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidLoad")
    }

    open fun viewWillAppear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewWillAppear", animated)
    }

    open fun viewDidAppear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidAppear", animated)
    }

    open fun viewWillDisappear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewWillDisappear", animated)
    }

    open fun viewDidDisappear(animated: Boolean) {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidDisappear", animated)
    }

    open fun viewWillLayoutSubviews() {
        EDOJavaHelper.invokeBindedMethod(this, "viewWillLayoutSubviews")
        EDOJavaHelper.emit(this, "viewWillLayoutSubviews", this)
    }

    open fun viewDidLayoutSubviews() {
        EDOJavaHelper.invokeBindedMethod(this, "viewDidLayoutSubviews")
    }

    var parentViewController: UIViewController? = null
        private set

    var presentedViewController: UIViewController? = null
        private set

    var presentingViewController: UIViewController? = null
        private set

    open fun presentViewController(viewController: UIViewController, animated: Boolean? = true, completion: EDOCallback? = null) {

    }

    open fun dismissViewController(animated: Boolean? = true, completion: EDOCallback? = null) {

    }

    var childViewControllers: List<UIViewController> = listOf()
        private set

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

    internal var window: UIWindow? = null
        get() {
            return field ?: parentViewController?.window
        }

}

fun KIMIPackage.installUIViewController() {
    exporter.exportClass(UIViewController::class.java, "UIViewController")
    exporter.exportProperty(UIViewController::class.java, "title")
    exporter.exportProperty(UIViewController::class.java, "view", true)
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidLoad")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillAppear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidAppear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillDisappear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidDisappear")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewWillLayoutSubviews")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "viewDidLayoutSubviews")
    exporter.exportProperty(UIViewController::class.java, "parentViewController", true)
    exporter.exportProperty(UIViewController::class.java, "presentedViewController", true)
    exporter.exportProperty(UIViewController::class.java, "presentingViewController", true)
    exporter.exportMethodToJavaScript(UIViewController::class.java, "presentViewController")
    exporter.exportMethodToJavaScript(UIViewController::class.java, "dismissViewController")
    exporter.exportProperty(UIViewController::class.java, "childViewControllers", true)
    exporter.exportMethodToJavaScript(UIViewController::class.java, "addChildViewController")
    exporter.exportMethodToJavaScript(UIViewController::class.java, "removeFromParentViewController")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "willMoveToParentViewController")
    exporter.bindMethodToJavaScript(UIViewController::class.java, "didMoveToParentViewController")
    exporter.exportProperty(UIViewController::class.java, "navigationController")
}