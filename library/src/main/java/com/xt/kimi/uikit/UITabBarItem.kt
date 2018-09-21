package com.xt.kimi.uikit

import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage

class UITabBarItem {

    var title: String? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "title")
            this.barButton?.setNeedUpdate()
        }

    var image: UIImage? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "image")
            this.barButton?.setNeedUpdate()
        }

    var selectedImage: UIImage? = null
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "selectedImage")
            this.barButton?.setNeedUpdate()
        }

    var imageInsets: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "imageInsets")
            this.barButton?.setNeedUpdate()
        }

    // Implementation

    internal var barButton: UITabBarButton? = null

}

fun KIMIPackage.installUITabBarItem() {
    exporter.exportClass(UITabBarItem::class.java, "UITabBarItem")
    exporter.exportProperty(UITabBarItem::class.java, "title", false, true, true)
    exporter.exportProperty(UITabBarItem::class.java, "image", false, true, true)
    exporter.exportProperty(UITabBarItem::class.java, "selectedImage", false, true, true)
    exporter.exportProperty(UITabBarItem::class.java, "imageInsets", false, true, true)
}