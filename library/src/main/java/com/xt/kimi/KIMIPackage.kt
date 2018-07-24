package com.xt.kimi

import com.xt.endo.EDOExporter
import com.xt.endo.EDOPackage
import com.xt.kimi.coregraphics.installCALayer
import com.xt.kimi.uikit.*

/**
 * Created by cuiminghui on 2018/7/20.
 */
class KIMIPackage : EDOPackage() {

    val exporter = EDOExporter.sharedExporter

    override fun install() {
        super.install()
        // UIKit
        installUIView()
        installUISwitch()
        installUIColor()
        installUIGestureRecognizer()
        installUITapGestureRecognizer()
        installUIPanGestureRecognizer()
        installUILongPressGestureRecognizer()
        installUIPinchGestureRecognizer()
        installUIRotationGestureRecognizer()
        // CoreGraphics
        installCALayer()
    }

}