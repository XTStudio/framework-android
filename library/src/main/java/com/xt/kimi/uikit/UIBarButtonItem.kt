package com.xt.kimi.uikit

import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UIBarButtonItem {

    private open inner class UIBarButton(buttonType: UIButtonType) : UIButton(buttonType)

    var title: String? = null
        set(value) {
            field = value
            this.resetText()
        }

    var titleAttributes: Map<String, Any>? = null
        set(value) {
            field = value
            this.resetText()
        }

    var image: UIImage? = null
        set(value) {
            field = value
            (this.customView as? UIBarButton)?.let {
                it.setImage(value, UIControlState.normal.rawValue)
            }
        }

    var tintColor: UIColor = UIColor.black
        set(value) {
            field = value
            (this.customView as? UIBarButton)?.let {
                it.tintColor = value
            }
        }

    var width: Double = 44.0

    var customView: UIView? = object : UIBarButton(UIButtonType.system) {
        override fun sendEvent(name: String) {
            super.sendEvent(name)
            if (name == "touchUpInside") {
                EDOJavaHelper.emit(this@UIBarButtonItem, "touchUpInside", this@UIBarButtonItem)
            }
        }
    }

    private fun resetText() {
        (this.customView as? UIBarButton)?.let { customView ->
            val title = this.title ?: kotlin.run {
                customView.setTitle(null, UIControlState.normal.rawValue)
                return@let
            }
            this.titleAttributes?.let {
                val attributedString = UIAttributedString(title, it)
                customView.setAttributedTitle(attributedString, UIControlState.normal.rawValue)
            } ?: kotlin.run {
                customView.setTitle(title, UIControlState.normal.rawValue)
            }
        }
    }

}

fun KIMIPackage.installUIBarButtonItem() {
    exporter.exportClass(UIBarButtonItem::class.java, "UIBarButtonItem")
    exporter.exportProperty(UIBarButtonItem::class.java, "title")
    exporter.exportProperty(UIBarButtonItem::class.java, "titleAttributes")
    exporter.exportProperty(UIBarButtonItem::class.java, "image")
    exporter.exportProperty(UIBarButtonItem::class.java, "tintColor")
    exporter.exportProperty(UIBarButtonItem::class.java, "width")
    exporter.exportProperty(UIBarButtonItem::class.java, "customView")
}