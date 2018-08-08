package com.xt.kimi.uikit

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import com.xt.endo.EDOCallback
import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage

open class UIViewController {

    var title: String? = null

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

    fun attachToActivity(activity: Activity) {
        val rootView = UIWindow()
        rootView.setBackgroundColor(Color.WHITE)
        rootView.rootViewController = this
        activity.setContentView(rootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
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
}