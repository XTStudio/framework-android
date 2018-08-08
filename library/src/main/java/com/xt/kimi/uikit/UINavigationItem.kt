package com.xt.kimi.uikit

class UINavigationItem {

    internal var viewController: UIViewController? = null
    internal var navigationBar: UINavigationBar? = null

    var title: String? = null
        set(value) {
            field = value
            this.setNeedsUpdate()
        }
        get() {
            return field ?: viewController?.title
        }

    var titleView: UIView = UILabel()
        set(value) {
            field?.removeFromSuperview()
            field = value
            this.setNeedsUpdate()
        }

    val backButton = object : UIButton(UIButtonType.system) {
        override fun sendEvent(name: String) {
            super.sendEvent(name)
            if (name == "touchUpInside") {
                this@UINavigationItem.viewController?.navigationController?.popViewController(true)
            }
        }
    }

    var hidesBackButton: Boolean = false
        set(value) {
            field = value
            this.setNeedsUpdate()
        }

    var leftBarButtonItem: UIBarButtonItem?
        get() {
            return this.leftBarButtonItems.first()
        }
        set(value) {
            value?.let {
                this.leftBarButtonItems = listOf(it)
            } ?: kotlin.run {
                this.leftBarButtonItems = listOf()
            }
        }

    var leftBarButtonItems: List<UIBarButtonItem> = listOf()
        set(value) {
            field = value
            this.setNeedsUpdate()
        }

    var rightBarButtonItem: UIBarButtonItem?
        get() {
            return this.rightBarButtonItems.first()
        }
        set(value) {
            value?.let {
                this.rightBarButtonItems = listOf(it)
            } ?: kotlin.run {
                this.rightBarButtonItems = listOf()
            }
        }

    var rightBarButtonItems: List<UIBarButtonItem> = listOf()
        set(value) {
            field = value
            this.setNeedsUpdate()
        }

    internal fun setNeedsUpdate() {
        (titleView as? UILabel)?.let {
            it.text = this.title
            it.font = (this.navigationBar?.titleTextAttributes?.get(UIAttributedStringKey.font.name) as? UIFont) ?: defaultTitleFont
        }
        this.navigationBar?.displayItems()
    }

    fun allViews(): List<UIView> {
        return listOf(
                backButton,
                titleView
        )
    }

    companion object {

        private val defaultTitleFont = UIFont(18.0)

    }

}