package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

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
            field.forEach { it.customView?.removeFromSuperview() }
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
            field.forEach { it.customView?.removeFromSuperview() }
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

    internal fun allViews(): List<UIView> {
        val views = mutableListOf<UIView>()
        views.add(backButton)
        views.add(titleView)
        this.leftViews().forEach { views.add(it) }
        this.rightViews().forEach { views.add(it) }
        return views.toList()
    }

    internal fun leftViews(): List<UIView> {
        return leftBarButtonItems.mapNotNull { it.customView }
    }

    internal fun rightViews(): List<UIView> {
        return rightBarButtonItems.mapNotNull { it.customView }
    }

    companion object {

        private val defaultTitleFont = UIFont(18.0)

    }

}

fun KIMIPackage.installUINavigationItem() {
    exporter.exportClass(UINavigationItem::class.java, "UINavigationItem")
    exporter.exportProperty(UINavigationItem::class.java, "title")
    exporter.exportProperty(UINavigationItem::class.java, "hidesBackButton")
    exporter.exportProperty(UINavigationItem::class.java, "leftBarButtonItem")
    exporter.exportProperty(UINavigationItem::class.java, "leftBarButtonItems")
    exporter.exportProperty(UINavigationItem::class.java, "rightBarButtonItem")
    exporter.exportProperty(UINavigationItem::class.java, "rightBarButtonItems")
}