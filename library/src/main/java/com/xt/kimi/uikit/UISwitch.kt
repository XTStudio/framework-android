package com.xt.kimi.uikit

import com.xt.endo.*
import com.xt.kimi.KIMIPackage

/**
 * Created by cuiminghui on 2018/7/20.
 */
class UISwitch: UIView() {

    inner class ThumbView: UIView() {

        override fun pointInside(point: CGPoint): Boolean {
            return point.x >= -22.0 && point.y >= -22.0 && point.x <= this.frame.width + 22.0 && point.y <= this.frame.height + 22.0
        }

    }

    var onTintColor: UIColor? = this.tintColor

    var thumbTintColor: UIColor? = UIColor.white

    var isOn: Boolean = false
        set(value) {
            field = value
            this.layoutSubviews()
        }

    val edo_isOn: Boolean
        get() {
            return isOn
        }

    fun edo_setOn(on: Boolean, animated: Boolean) {
        if (animated) {
            UIAnimator.shared.curve(0.20, EDOCallback.createWithBlock { this.isOn = on }, null)
        }
        else {
            this.isOn = on
        }
    }

    // Implementation

    private var tintView = UIView()
    private var thumbView = ThumbView()
    private var thumbOutLightView = UIView()
    private var tracking = false
        set(value) {
            if (field == value) { return }
            field = value
            UIAnimator.shared.linear(0.15, EDOCallback.createWithBlock {
                if (value) {
                    this.thumbOutLightView.transform = CGAffineTransform(1.6, 0.0, 0.0, 1.6, 0.0, 0.0)
                }
                else {
                    this.thumbOutLightView.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                }
            }, null)
        }

    init {
        this.addSubview(this.tintView)
        this.addSubview(this.thumbOutLightView)
        this.thumbView.layer.shadowColor = UIColor(0.0, 0.0, 0.0, 1.0)
        this.thumbView.layer.shadowRadius = 2.0
        this.thumbView.layer.shadowOffset = CGSize(0.0, 3.0)
        this.thumbView.layer.shadowOpacity = 0.2
        this.addSubview(this.thumbView)
        this.setupTouches()
    }

    private var touchChanged = false

    fun setupTouches() {
        val longPressGesture = object: UILongPressGestureRecognizer() {
            override fun handleEvent(name: String) {
                super.handleEvent(name)
                if (name == "began") {
                    this@UISwitch.touchChanged = false
                    this@UISwitch.tracking = true
                }
                else if (name == "changed") {
                    val location = this.locationInView(this@UISwitch)
                    val isOn = location.x > this@UISwitch.bounds.width / 2.0
                    if (this@UISwitch.isOn != isOn) {
                        this@UISwitch.touchChanged = true
                        UIAnimator.shared.curve(0.20, EDOCallback.createWithBlock { this@UISwitch.isOn = isOn }, null)
                    }
                }
                else if (name == "ended") {
                    if (!this@UISwitch.touchChanged) {
                        val location = this.locationInView(this@UISwitch)
                        if (this@UISwitch.pointInside(location)) {
                            UIAnimator.shared.curve(0.20, EDOCallback.createWithBlock { this@UISwitch.isOn = !this@UISwitch.isOn }, EDOCallback.createWithBlock {
                                EDOJavaHelper.emit(this@UISwitch, "valueChanged", this@UISwitch)
                            })
                        }
                    }
                    else {
                        EDOJavaHelper.emit(this@UISwitch, "valueChanged", this@UISwitch)
                    }
                    this@UISwitch.tracking = false
                }
                else if (name == "cancelled") {
                    if (this@UISwitch.touchChanged) {
                        EDOJavaHelper.emit(this@UISwitch, "valueChanged", this@UISwitch)
                    }
                    this@UISwitch.tracking = false
                }
            }
        }
        longPressGesture.minimumPressDuration = 0.0
        this.thumbView.addGestureRecognizer(longPressGesture)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.tintView.frame = CGRect((this.bounds.width - 34.0) / 2.0, (this.bounds.height - 14.0) / 2.0, 34.0, 14.0)
        this.tintView.layer.cornerRadius = 7.0
        if (this.isOn) {
            this.thumbView.frame = CGRect(this.tintView.frame.x + this.tintView.frame.width - 20.0, (this.bounds.height - 20.0) / 2.0, 20.0, 20.0)
            this.thumbView.layer.cornerRadius = 10.0
            this.thumbView.edo_backgroundColor = this.onTintColor
            this.tintView.edo_backgroundColor = this.onTintColor?.colorWithAlphaComponent(0.5)
            this.thumbOutLightView.frame = CGRect(this.tintView.frame.x + this.tintView.frame.width - 20.0, (this.bounds.height - 20.0) / 2.0, 20.0, 20.0)
            this.thumbOutLightView.layer.cornerRadius = 10.0
            this.thumbOutLightView.edo_backgroundColor = this.onTintColor?.colorWithAlphaComponent(0.2)
        }
        else {
            this.thumbView.frame = CGRect(this.tintView.frame.x, (this.bounds.height - 20.0) / 2.0, 20.0, 20.0)
            this.thumbView.layer.cornerRadius = 10.0
            this.thumbView.edo_backgroundColor = this.thumbTintColor
            this.tintView.edo_backgroundColor = UIColor(0x84 / 255.0, 0x84 / 255.0, 0x84 / 255.0, 1.0)
            this.thumbOutLightView.frame = CGRect(this.tintView.frame.x, (this.bounds.height - 20.0) / 2.0, 20.0, 20.0)
            this.thumbOutLightView.layer.cornerRadius = 10.0
            this.thumbOutLightView.edo_backgroundColor = UIColor(0x84 / 255.0, 0x84 / 255.0, 0x84 / 255.0, 0.2)
        }
    }

    override fun pointInside(point: CGPoint): Boolean {
        return point.x >= 0.0 && point.y >= -(44.0 - this.frame.height) / 2.0 && point.x <= this.frame.width && point.y <= this.frame.height + (44.0 - this.frame.height) / 2.0
    }

}

fun KIMIPackage.installUISwitch() {
    exporter.exportClass(UISwitch::class.java, "UISwitch", "UIView")
    exporter.exportProperty(UISwitch::class.java, "onTintColor")
    exporter.exportProperty(UISwitch::class.java, "thumbTintColor")
    exporter.exportProperty(UISwitch::class.java, "edo_isOn")
    exporter.exportMethodToJavaScript(UISwitch::class.java, "edo_setOn")
}