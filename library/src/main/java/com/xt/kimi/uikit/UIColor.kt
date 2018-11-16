package com.xt.kimi.uikit

import android.graphics.Color
import com.xt.kimi.KIMIPackage
import java.lang.Exception
import kotlin.math.abs

/**
 * Created by cuiminghui on 2018/7/20.
 */
class UIColor(val r: Double, val g: Double, val b: Double, val a: Double) {

    fun toInt(): Int {
        if (abs(this.a - 0.0) < 0.0001) {
            return Color.TRANSPARENT
        }
        return Color.argb(Math.ceil(this.a * 255.0).toInt(), Math.ceil(this.r * 255.0).toInt(), Math.ceil(this.g * 255.0).toInt(), Math.ceil(this.b * 255.0).toInt())
    }

    fun colorWithAlphaComponent(value: Double): UIColor {
        return UIColor(r, g, b, a * value)
    }

    companion object {

        @JvmStatic fun hexColor(hexValue: String): UIColor {
            val trimmedValue = hexValue.replace("#", "")
            if (trimmedValue.length == 6) {
                return try {
                    return UIColor(
                            trimmedValue.substring(0, 2).toInt(16).toDouble() / 255.0,
                            trimmedValue.substring(2, 4).toInt(16).toDouble() / 255.0,
                            trimmedValue.substring(4, 6).toInt(16).toDouble() / 255.0,
                            1.0
                    )
                } catch (e: Exception) { UIColor.clear }
            }
            else if (trimmedValue.length == 8) {
                return try {
                    return UIColor(
                            trimmedValue.substring(2, 4).toInt(16).toDouble() / 255.0,
                            trimmedValue.substring(4, 6).toInt(16).toDouble() / 255.0,
                            trimmedValue.substring(6, 8).toInt(16).toDouble() / 255.0,
                            trimmedValue.substring(0, 2).toInt(16).toDouble() / 255.0
                    )
                } catch (e: Exception) { UIColor.clear }
            }
            return clear
        }

        @JvmStatic val black = UIColor(0.0, 0.0, 0.0, 1.0)
        @JvmStatic val clear = UIColor(0.0, 0.0, 0.0, 0.0)
        @JvmStatic val gray = UIColor(0.5, 0.5, 0.5, 1.0)
        @JvmStatic val red = UIColor(1.0, 0.0, 0.0, 1.0)
        @JvmStatic val yellow = UIColor(1.0, 1.0, 0.0, 1.0)
        @JvmStatic val green = UIColor(0.0, 1.0, 0.0, 1.0)
        @JvmStatic val blue = UIColor(0.0, 0.0, 1.0, 1.0)
        @JvmStatic val white = UIColor(1.0, 1.0, 1.0, 1.0)

    }

}

fun KIMIPackage.installUIColor() {
    exporter.exportClass(UIColor::class.java, "UIColor")
    exporter.exportStaticMethodToJavaScript(UIColor::class.java, "hexColor")
    exporter.exportInitializer(UIColor::class.java) {
        val r: Double = if (0 < it.count() && it[0] is Number) (it[0] as Number).toDouble() else 0.0
        val g: Double = if (1 < it.count() && it[1] is Number) (it[1] as Number).toDouble() else 0.0
        val b: Double = if (2 < it.count() && it[2] is Number) (it[2] as Number).toDouble() else 0.0
        val a: Double = if (3 < it.count() && it[3] is Number) (it[3] as Number).toDouble() else 1.0
        return@exportInitializer UIColor(r, g, b, a)
    }
    exporter.exportStaticProperty(UIColor::class.java, "black", true)
    exporter.exportStaticProperty(UIColor::class.java, "clear", true)
    exporter.exportStaticProperty(UIColor::class.java, "gray", true)
    exporter.exportStaticProperty(UIColor::class.java, "red", true)
    exporter.exportStaticProperty(UIColor::class.java, "yellow", true)
    exporter.exportStaticProperty(UIColor::class.java, "green", true)
    exporter.exportStaticProperty(UIColor::class.java, "blue", true)
    exporter.exportStaticProperty(UIColor::class.java, "white", true)
}