package com.xt.kimi

import com.xt.endo.EDOExporter
import com.xt.endo.EDOPackage
import com.xt.kimi.uikit.installUIColor
import com.xt.kimi.uikit.installUISwitch
import com.xt.kimi.uikit.installUIView

/**
 * Created by cuiminghui on 2018/7/20.
 */
class KIMIPackage : EDOPackage() {

    val exporter = EDOExporter.sharedExporter

    override fun install() {
        super.install()
        installUIView()
        installUISwitch()
        installUIColor()
    }

}