package com.xt.kimi.uikit

import android.app.AlertDialog
import android.graphics.Color
import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import com.xt.kimi.coregraphics.CAShapeLayer
import com.xt.kimi.currentActivity
import com.xt.kimi.foundation.DispatchQueue

class UIAlertAction(val title: String, val style: Style, val callback: () -> Unit) {

    enum class Style {
        normal,
        danger,
        cancel,
    }

}

internal class UIActionSheetController: UIViewController() {

    val backgroundView = UIView()
    val contentView = UIView()

    override fun viewDidLoad() {
        super.viewDidLoad()
        this.view.edo_backgroundColor = UIColor.clear
        this.backgroundView.edo_backgroundColor = UIColor(0.0, 0.0, 0.0, 0.35)
        this.view.addSubview(backgroundView)
        this.contentView.tintColor = UIColor.black
        this.contentView.edo_backgroundColor =  UIColor(0.9, 0.9, 0.9, 1.0)
        this.view.addSubview(contentView)
    }

    var message: String? = null

    var actions: MutableList<UIAlertAction> = mutableListOf()
        set(value) {
            field = value
            this.setupContents()
        }

    fun setupContents() {
        var height = 0.0
        this.message?.let {
            val messageView = UIView()
            messageView.edo_backgroundColor = UIColor.white
            val textLabel = UILabel()
            val attributedString = UIAttributedString(it, mapOf(
                    Pair(UIAttributedStringKey.font.name, UIFont(14.0)),
                    Pair(UIAttributedStringKey.foregroundColor.name, UIColor(0.55, 0.55, 0.55, 1.0)),
                    Pair(UIAttributedStringKey.paragraphStyle.name, kotlin.run {
                        val p = UIParagraphStyle()
                        p.alignment = UITextAlignment.center
                        return@run p
                    })
            ))
            val textBounds = attributedString.measure(CGSize(UIScreen.main.bounds.width - 60, 88.0))
            textLabel.attributedText = attributedString
            textLabel.numberOfLines = 0
            textLabel.frame = CGRect(30.0, 0.0, UIScreen.main.bounds.width - 60, textBounds.height + 28)
            messageView.frame = CGRect(0.0, height, 0.0, textBounds.height + 28)
            messageView.addSubview(textLabel)
            val hrLayer = CAShapeLayer()
            hrLayer.lineWidth = 1.0
            hrLayer.strokeColor = UIColor(0.8, 0.8, 0.8, 1.0)
            messageView.layer.addSublayer(hrLayer)
            contentView.addSubview(messageView)
            height += messageView.frame.height
        }
        this.actions.forEach {
            if (it.style == UIAlertAction.Style.cancel) {
                height += 6.0
            }
            val view = object :UIButton(UIButtonType.system) {
                override fun sendEvent(name: String) {
                    super.sendEvent(name)
                    if (name == "touchUpInside") {
                        this@UIActionSheetController.dismiss(true) {
                            it.callback()
                        }
                    }
                }
            }
            view.edo_backgroundColor = UIColor.white
            view.setTitle(it.title, UIControlState.normal.rawValue)
            view.setTitleFont(UIFont(19.0))
            if (it.style == UIAlertAction.Style.danger) {
                view.setTitleColor(UIColor(231.0 / 255.0, 45.0 / 255.0, 39.0 / 255.0, 1.0), UIControlState.normal.rawValue)
            }
            view.frame = CGRect(0.0, height, 0.0, 55.0)
            val hrLayer = CAShapeLayer()
            hrLayer.lineWidth = 1.0
            hrLayer.strokeColor = UIColor(0.9, 0.9, 0.9, 1.0)
            view.layer.addSublayer(hrLayer)
            contentView.addSubview(view)
            height += 55.0
        }
        contentView.frame = CGRect(0.0, 0.0, 0.0, height)
        this.actions.firstOrNull { it.style == UIAlertAction.Style.cancel }?.let { cancelAction ->
            backgroundView.addGestureRecognizer(object : UITapGestureRecognizer() {
                override fun handleEvent(name: String) {
                    super.handleEvent(name)
                    if (name == "touch") {
                        this@UIActionSheetController.dismiss(true) {
                            cancelAction.callback()
                        }
                    }
                }
            })
        }
    }

    fun show(animated: Boolean) {
        if (animated) {
            backgroundView.edo_alpha = 0.0
            contentView.edo_alpha = 0.0
            DispatchQueue.main.asyncAfter(0.10, EDOCallback.createWithBlock {
                contentView.edo_alpha = 1.0
                contentView.frame = CGRect(0.0, this.view.bounds.height, this.view.bounds.width, contentView.frame.height)
                UIAnimator.shared.curve(0.3, EDOCallback.createWithBlock {
                    backgroundView.edo_alpha = 1.0
                    contentView.frame = CGRect(0.0, this.view.bounds.height - contentView.frame.height, this.view.bounds.width, contentView.frame.height)
                }, null)
            })
        }
    }

    fun dismiss(animated: Boolean, callback: () -> Unit) {
        UIActionSheet.currentActionSheet = null
        if (animated) {
            backgroundView.edo_alpha = 1.0
            contentView.frame = CGRect(0.0, this.view.bounds.height - contentView.frame.height, this.view.bounds.width, contentView.frame.height)
            UIAnimator.shared.curve(0.3, EDOCallback.createWithBlock {
                backgroundView.edo_alpha = 0.0
                contentView.frame = CGRect(0.0, this.view.bounds.height, this.view.bounds.width, contentView.frame.height)
            }, EDOCallback.createWithBlock {
                this.detachFromActivity()
                callback()
            })
        }
        else {
            this.detachFromActivity()
        }
    }

    override fun viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        backgroundView.frame = this.view.bounds
        contentView.frame = CGRect(0.0, this.view.bounds.height - contentView.frame.height, this.view.bounds.width, contentView.frame.height)
        contentView.subviews.forEach {
            it.frame = CGRect(it.frame.x, it.frame.y, this.view.bounds.width, it.frame.height)
            val bezierPath = UIBezierPath()
            bezierPath.edo_moveTo(CGPoint(0.0, it.bounds.height))
            bezierPath.edo_addLineTo(CGPoint(this.view.bounds.width, it.bounds.height))
            (it.layer.sublayers[0] as? CAShapeLayer)?.path = bezierPath
        }
    }

}

class UIActionSheet {

    var message: String = ""

    val actions: MutableList<UIAlertAction> = mutableListOf()

    fun addRegularAction(title: String, actionBlock: EDOCallback?) {
        this.actions.add(UIAlertAction(title, UIAlertAction.Style.normal) {
            actionBlock?.invoke()
        })
    }

    fun addDangerAction(title: String, actionBlock: EDOCallback?) {
        this.actions.add(UIAlertAction(title, UIAlertAction.Style.danger) {
            actionBlock?.invoke()
        })
    }

    fun addCancelAction(title: String, actionBlock: EDOCallback?) {
        this.actions.add(UIAlertAction(title, UIAlertAction.Style.cancel) {
            actionBlock?.invoke()
        })
    }

    fun show() {
        currentActionSheet?.let { it.dismiss(false) {} }
        currentActivity?.let {
            val view = UIActionSheetController()
            UIActionSheet.currentActionSheet = view
            view.attachToActivity(it, true, false)
            view.message = this.message?.takeIf { !it.isEmpty() }
            view.actions = this.actions
            view.show(true)
        }
    }

    companion object {

        internal var currentActionSheet: UIActionSheetController? = null

    }

}

fun KIMIPackage.installUIActionSheet() {
    exporter.exportClass(UIActionSheet::class.java, "UIActionSheet")
    exporter.exportProperty(UIActionSheet::class.java, "message")
    exporter.exportMethodToJavaScript(UIActionSheet::class.java, "addRegularAction")
    exporter.exportMethodToJavaScript(UIActionSheet::class.java, "addDangerAction")
    exporter.exportMethodToJavaScript(UIActionSheet::class.java, "addCancelAction")
    exporter.exportMethodToJavaScript(UIActionSheet::class.java, "show")
}