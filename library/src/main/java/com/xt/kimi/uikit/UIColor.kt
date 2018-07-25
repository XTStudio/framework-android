package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

/**
 * Created by cuiminghui on 2018/7/20.
 */
class UIColor(val r: Double, val g: Double, val b: Double, val a: Double)

fun KIMIPackage.installUIColor() {
    exporter.exportClass(UIColor::class.java, "UIColor")
    exporter.exportInitializer(UIColor::class.java, {
        val r: Double = if (0 < it.count() && it[0] is Number) (it[0] as Number).toDouble() else 0.0
        val g: Double = if (1 < it.count() && it[1] is Number) (it[1] as Number).toDouble() else 0.0
        val b: Double = if (2 < it.count() && it[2] is Number) (it[2] as Number).toDouble() else 0.0
        val a: Double = if (3 < it.count() && it[3] is Number) (it[3] as Number).toDouble() else 1.0
        return@exportInitializer UIColor(r, g, b, a)
    })
    exporter.exportScript(UIColor::class.java, "Initializer.black = new Initializer(0,0,0,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.clear = new Initializer(0,0,0,0)")
    exporter.exportScript(UIColor::class.java, "Initializer.gray = new Initializer(0.5,0.5,0.5,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.red = new Initializer(1,0,0,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.yellow = new Initializer(1,1,0,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.green = new Initializer(0,1,0,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.blue = new Initializer(0,0,1,1)")
    exporter.exportScript(UIColor::class.java, "Initializer.white = new Initializer(1,1,1,1)")
}