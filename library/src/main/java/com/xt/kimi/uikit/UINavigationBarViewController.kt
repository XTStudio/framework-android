package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UINavigationBarViewController: UIViewController() {

    internal class UINavigationControllerState {

        var barHidden = false

    }

    var navigationBarContentHeight: Double = 44.0
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "navigationBarContentHeight")
        }

    var navigationBarInFront: Boolean = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "navigationBarInFront")
            if (value) {
                this.view.bringSubviewToFront(this.navigationBar)
            }
            else {
                this.view.bringSubviewToFront(this.contentView)
            }
        }

    val navigationBar: UIView = UIView()

    val contentView: UIView = UIView()

    val edo_view: UIView
        get() {
            return this.contentView
        }

    internal var navigationControllerState: UINavigationControllerState? = null

    override fun loadView() {
        super.loadView()
        this.view.addSubview(this.contentView)
        this.navigationBar.isImportantNodeForRendering = true
        this.view.addSubview(this.navigationBar)
    }

    override fun viewWillAppear(animated: Boolean) {
        if (this.navigationController != null && this.navigationControllerState == null) {
            this.navigationController?.let { navigationController ->
                this.navigationControllerState = UINavigationControllerState()
                this.navigationControllerState?.barHidden = navigationController.navigationBar.hidden
                navigationController.setNavigationBarHidden(true, animated, false)
            }
        }
        super.viewWillAppear(animated)
    }

    override fun viewWillDisappear(animated: Boolean) {
        this.navigationControllerState?.let { navigationControllerState ->
            this.navigationController?.let { navigationController ->
                navigationController.setNavigationBarHidden(navigationControllerState.barHidden, animated, true)
            }
        }
        super.viewWillDisappear(animated)
    }

    private val barFrame: CGRect
        get() {
            if (this.navigationBar.hidden) {
                return CGRect(0.0, 0.0, this.view.bounds.width, 0.0)
            }
            return CGRect(0.0, 0.0, this.view.bounds.width, (this.window?.statusBarHeight ?: 0.0) + this.navigationBarContentHeight)
        }

    private val contentFrame: CGRect
        get() {
            return CGRect(0.0, this.barFrame.height, this.view.bounds.width, this.view.bounds.height - this.barFrame.height)
        }

    override fun viewWillLayoutSubviews() {
        this.navigationBar.frame = barFrame
        this.contentView.frame = contentFrame
        super.viewWillLayoutSubviews()
    }

}

fun KIMIPackage.installUINavigationBarViewController() {
    exporter.exportClass(UINavigationBarViewController::class.java, "UINavigationBarViewController", "UIViewController")
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBarContentHeight", false, true, true)
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBarInFront", false, true, true)
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBar", true, true)
    exporter.exportProperty(UINavigationBarViewController::class.java, "edo_view", true, true)
}