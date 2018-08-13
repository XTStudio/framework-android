package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.kimi.KIMIPackage

class UINavigationBarViewController: UIViewController() {

    private class UINavigationControllerState {

        var barHidden = false

    }

    var navigationBarContentHeight: Double = 44.0

    var navigationBarInFront: Boolean = true
        set(value) {
            field = value
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

    private var navigationControllerState: UINavigationControllerState? = null

    override fun loadView() {
        super.loadView()
        this.view.addSubview(this.contentView)
        this.view.addSubview(this.navigationBar)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        if (this.navigationController != null && this.navigationControllerState == null) {
            this.navigationController?.let { navigationController ->
                this.navigationControllerState = UINavigationControllerState()
                this.navigationControllerState?.barHidden = navigationController.navigationBar.hidden
                navigationController.setNavigationBarHidden(true, animated, false)
            }
        }
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        this.navigationControllerState?.let { navigationControllerState ->
            this.navigationController?.let { navigationController ->
                navigationController.setNavigationBarHidden(navigationControllerState.barHidden, animated, true)
            }
        }
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
        super.viewWillLayoutSubviews()
        this.navigationBar.frame = barFrame
        this.contentView.frame = contentFrame
    }

}

fun KIMIPackage.installUINavigationBarViewController() {
    exporter.exportClass(UINavigationBarViewController::class.java, "UINavigationBarViewController", "UIViewController")
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBarContentHeight")
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBarInFront")
    exporter.exportProperty(UINavigationBarViewController::class.java, "navigationBar", true)
    exporter.exportProperty(UINavigationBarViewController::class.java, "edo_view", true)
}