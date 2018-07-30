package com.xt.kimi.uikit

import android.graphics.Canvas
import android.view.ViewGroup
import android.widget.Switch
import com.xt.kimi.KIMIPackage

/**
 * Created by cuiminghui on 2018/7/20.
 */
class UISwitch: UIView() {

    init {
        addView(Switch(this.context), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

}

fun KIMIPackage.installUISwitch() {
    exporter.exportClass(UISwitch::class.java, "UISwitch", "UIView")
}