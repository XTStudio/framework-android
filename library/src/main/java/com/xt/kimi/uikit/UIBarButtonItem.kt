package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UIBarButtonItem {

    var title: String? = null

    var titleAttributes: Map<String, Any> = mapOf()

    var image: UIImage? = null

    var tintColor: UIColor = UIColor.black

    var width: Double = 44.0

    var customView: UIView? = null

}

fun KIMIPackage.installUIBarButtonItem() {
    exporter.exportClass(UIBarButtonItem::class.java, "UIBarButtonItem")
}