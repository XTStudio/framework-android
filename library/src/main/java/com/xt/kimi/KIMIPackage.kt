package com.xt.kimi

import android.app.Activity
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.xt.endo.CGRect
import com.xt.endo.EDOExporter
import com.xt.endo.EDOPackage
import com.xt.kimi.coregraphics.installCADisplayLink
import com.xt.kimi.coregraphics.installCAGradientLayer
import com.xt.kimi.coregraphics.installCALayer
import com.xt.kimi.coregraphics.installCAShapeLayer
import com.xt.kimi.foundation.*
import com.xt.kimi.kimi.KMCore
import com.xt.kimi.kimi.installKMCore
import com.xt.kimi.uikit.*
import com.xt.kimi.uikit.helper.KeyboardHeightObserver
import com.xt.kimi.uikit.helper.KeyboardHeightProvider

/**
 * Created by cuiminghui on 2018/7/20.
 */

var currentActivity: Activity? = null
    private set

class KIMIPackage : EDOPackage(), KeyboardHeightObserver {

    val exporter = EDOExporter.sharedExporter

    private var keyboardHeightProvider: KeyboardHeightProvider? = null

    override fun install() {
        super.install()
        val applicationContext = exporter.applicationContext ?: return
        // KIMI
        installKMCore()
        KMCore.hostVersion = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        // Foundation
        installDispatchQueue()
        installUUID()
        installURL()
        installURLRequest()
        installURLResponse()
        installURLSession()
        installData()
        installUserDefaults()
        installBundle()
        installTimer()
        installFileManager()
        // UIKit
        (applicationContext as? Application)?.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
                keyboardHeightProvider?.close()
                keyboardHeightProvider = null
            }
            override fun onActivityResumed(activity: Activity?) {
                currentActivity = activity
                keyboardHeightProvider = KeyboardHeightProvider(activity)
                activity?.findViewById<View>(android.R.id.content)?.post {
                    keyboardHeightProvider?.start()
                }
                keyboardHeightProvider?.setKeyboardHeightObserver(this@KIMIPackage)
            }
            override fun onActivityStarted(activity: Activity?) {
                currentActivity = activity
            }
            override fun onActivityDestroyed(activity: Activity?) { }
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) { }
            override fun onActivityStopped(activity: Activity?) { }
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
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
        installUIAttributedString()
        installUIParagraphStyle()
        installUIViewController()
        installUINavigationController()
        installUINavigationBar()
        installUIBarButtonItem()
        installUINavigationItem()
        installUITabBarController()
        installUITabBar()
        installUITabBarItem()
        installUIPageViewController()
        installUINavigationBarViewController()
        installUIRefreshControl()
        installUIFetchMoreControl()
        installUIProgressView()
        // CoreGraphics
        installCALayer()
        installCAGradientLayer()
        installCADisplayLink()
        installCAShapeLayer()
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val keyboardHeight = height.toDouble() / scale
        val contentView = currentActivity?.findViewById<View>(android.R.id.content) as? ViewGroup ?: return
        ((contentView as? UIWindow) ?: (contentView.getChildAt(0) as? UIWindow))?.let {
            if (keyboardHeight > 0) {
                it.keyboardWillShow(keyboardHeight)
            }
            else {
                it.keyboardWillHide()
            }
        }
    }

}