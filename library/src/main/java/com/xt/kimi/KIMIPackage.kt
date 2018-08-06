package com.xt.kimi

import android.app.Activity
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import com.xt.endo.CGRect
import com.xt.endo.EDOExporter
import com.xt.endo.EDOPackage
import com.xt.kimi.coregraphics.*
import com.xt.kimi.foundation.*
import com.xt.kimi.uikit.*

/**
 * Created by cuiminghui on 2018/7/20.
 */

var currentActivity: Activity? = null
    private set

class KIMIPackage : EDOPackage() {

    val exporter = EDOExporter.sharedExporter

    override fun install() {
        super.install()
        val applicationContext = exporter.applicationContext ?: return
        // Foundation
        installDispatchQueue()
        installUUID()
        installURL()
        installURLRequest()
        installData()
        installUserDefaults()
        installBundle()
        // UIKit
        (applicationContext as? Application)?.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) { }
            override fun onActivityResumed(activity: Activity?) {
                currentActivity = activity
            }
            override fun onActivityStarted(activity: Activity?) {
                currentActivity = activity
            }
            override fun onActivityDestroyed(activity: Activity?) { }
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) { }
            override fun onActivityStopped(activity: Activity?) { }
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) { }
        })
        scale = applicationContext.resources.displayMetrics.density
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
        installUIButton()
        UIScreen.main.scale = scale.toDouble()
        UIScreen.main.bounds = CGRect(
                0.0,
                0.0,
                (applicationContext.resources.displayMetrics.widthPixels).toDouble() / scale,
                (applicationContext.resources.displayMetrics.heightPixels).toDouble() / scale
        )
        installUIScreen()
        val sharedPreferences = applicationContext.getSharedPreferences("com.xt.kimi.installtion", MODE_PRIVATE)
        sharedPreferences.getString("identifierForVendor", null)?.let {
            UIDevice.current.identifierForVendor = UUID(it)
        } ?: kotlin.run {
            UIDevice.current.identifierForVendor = UUID()
            sharedPreferences.edit().putString("identifierForVendor", UIDevice.current.identifierForVendor?.UUIDString).apply()
        }
        installUIDevice()
        installUIScrollView()
        installUIAlert()
        installUIPrompt()
        installUIConfirm()
        installUIActivityIndicatorView()
        installUISlider()
        installUITextField()
        installUITextView()
        installUITableView()
        installUITableViewCell()
        installUIIndexPath()
        installUIWebView()
        installUIStackView()
        installUICollectionView()
        installUICollectionViewLayout()
        installUICollectionViewCell()
        installUICollectionReusableView()
        // CoreGraphics
        installCALayer()
        installCAGradientLayer()
        installCADisplayLink()
        installCAShapeLayer()
    }

}