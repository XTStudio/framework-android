package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UISlider: UIView() {

    var value: Double = 0.0

    var minimumValue: Double = 0.0

    var maximumValue: Double = 1.0

    var minimumTrackTintColor: UIColor? = null

    var maximumTrackTintColor: UIColor? = null

    var thumbTintColor: UIColor? = null

    fun edo_setValue(value: Double, animated: Boolean) {

    }

    // Implementation

}

fun KIMIPackage.installUISlider() {
    exporter.exportClass(UISlider::class.java, "UISlider", "UIView")
}