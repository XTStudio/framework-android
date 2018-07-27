package com.xt.kimi

import android.content.Context.MODE_PRIVATE
import com.xt.endo.CGRect
import com.xt.endo.EDOExporter
import com.xt.endo.EDOPackage
import com.xt.kimi.coregraphics.*
import com.xt.kimi.foundation.*
import com.xt.kimi.uikit.*

/**
 * Created by cuiminghui on 2018/7/20.
 */
class KIMIPackage : EDOPackage() {

    val exporter = EDOExporter.sharedExporter

    override fun install() {
        super.install()
        // Foundation
        installDispatchQueue()
        installUUID()
        // UIKit
        scale = exporter.applicationContext?.resources?.displayMetrics?.density ?: 1.0f
        installUIView()
        installUISwitch()
        installUIColor()
        installUIGestureRecognizer()
        installUITapGestureRecognizer()
        installUIPanGestureRecognizer()
        installUILongPressGestureRecognizer()
        installUIPinchGestureRecognizer()
        installUIRotationGestureRecognizer()
        installUIAnimator()
        installUIImage()
        installUIImageView()
        installUILabel()
        installUIFont()
        installUIBezierPath()
        UIScreen.main.scale = scale.toDouble()
        exporter.applicationContext?.let {
            UIScreen.main.bounds = CGRect(
                    0.0,
                    0.0,
                    (it.resources.displayMetrics.widthPixels).toDouble() / scale,
                    (it.resources.displayMetrics.heightPixels).toDouble() / scale
            )
        }
        installUIScreen()
        exporter.applicationContext?.let {
            val sharedPreferences = it.getSharedPreferences("com.xt.kimi.installtion", MODE_PRIVATE)
            sharedPreferences.getString("identifierForVendor", null)?.let {
                UIDevice.current.identifierForVendor = UUID(it)
            } ?: kotlin.run {
                UIDevice.current.identifierForVendor = UUID()
                sharedPreferences.edit().putString("identifierForVendor", UIDevice.current.identifierForVendor?.UUIDString).apply()
            }
        }
        installUIDevice()
        // CoreGraphics
        installCALayer()
        installCAGradientLayer()
        installCADisplayLink()
        installCAShapeLayer()
    }

}