package com.xt.kimi.uikit

import com.xt.endo.*
import com.xt.kimi.KIMIPackage
import kotlin.math.max
import kotlin.math.min

class UISlider: UIView() {

    inner class ThumbView: UIView() {

        override fun pointInside(point: CGPoint): Boolean {
            return point.x >= -22.0 && point.y >= -22.0 && point.x <= this.frame.width + 22.0 && point.y <= this.frame.height + 22.0
        }

    }

    var value: Double = 0.5

    var minimumValue: Double = 0.0

    var maximumValue: Double = 1.0

    var minimumTrackTintColor: UIColor? = null
        set(value) {
            field = value
            this.minimumTrackView.edo_backgroundColor = value
        }

    var maximumTrackTintColor: UIColor? = null
        set(value) {
            field = value
            this.maximumTrackView.edo_backgroundColor = value
        }

    var thumbTintColor: UIColor? = null
        set(value) {
            field = value
            this.thumbView.edo_backgroundColor = value
            this.thumbOutLightView.edo_backgroundColor = value?.colorWithAlphaComponent(0.2)
        }

    fun edo_setValue(value: Double, animated: Boolean) {
        if (animated) {
            this.value = value
            UIAnimator.shared.curve(0.5, EDOCallback.createWithBlock { this.layoutSubviews() }, null)
        }
        else {
            this.value = value
            this.layoutSubviews()
        }
    }

    // Implementation

    private var minimumTrackView = UIView()
    private var maximumTrackView = UIView()
    private var thumbView = ThumbView()
    private var thumbOutLightView = UIView()
    private var tracking = false
        set(value) {
            if (field == value) { return }
            field = value
            UIAnimator.shared.linear(0.15, EDOCallback.createWithBlock {
                if (value) {
                    this.thumbView.transform = CGAffineTransform(1.4, 0.0, 0.0, 1.4, 0.0, 0.0)
                    this.thumbOutLightView.transform = CGAffineTransform(2.4, 0.0, 0.0, 2.4, 0.0, 0.0)
                }
                else {
                    this.thumbView.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                    this.thumbOutLightView.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                }
            }, null)
        }

    init {
        this.tintColor?.let { tintColor ->
            this.minimumTrackTintColor = tintColor
            this.maximumTrackTintColor = tintColor.colorWithAlphaComponent(0.3)
            this.thumbTintColor = tintColor
        }
        this.maximumTrackView.userInteractionEnabled = false
        this.addSubview(this.maximumTrackView)
        this.minimumTrackView.userInteractionEnabled = false
        this.addSubview(this.minimumTrackView)
        this.addSubview(this.thumbOutLightView)
        this.addSubview(this.thumbView)
        this.setupTouches()
    }

    private var previousLocation: CGPoint? = null

    fun setupTouches() {
        val longPressGesture = object: UILongPressGestureRecognizer() {
            override fun handleEvent(name: String) {
                super.handleEvent(name)
                if (name == "began") {
                    this@UISlider.previousLocation = this.locationInView(this@UISlider)
                    this@UISlider.tracking = true
                }
                else if (name == "changed") {
                    val previousLocation = this@UISlider.previousLocation ?: return
                    val location = this.locationInView(this@UISlider)
                    val translationX = location.x - previousLocation.x
                    this@UISlider.previousLocation = location
                    val newValue = this@UISlider.value + translationX / this@UISlider.frame.width * (this@UISlider.maximumValue - this@UISlider.minimumValue)
                    this@UISlider.value = max(this@UISlider.minimumValue, min(this@UISlider.maximumValue, newValue))
                    EDOJavaHelper.emit(this@UISlider, "valueChanged", this@UISlider)
                    this@UISlider.layoutSubviews()
                }
                else if (name == "ended") {
                    this@UISlider.tracking = false
                }
                else if (name == "cancelled") {
                    this@UISlider.tracking = false
                }
            }
        }
        longPressGesture.minimumPressDuration = 0.0
        this.thumbView.addGestureRecognizer(longPressGesture)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        val progress = max(0.0, min(1.0, (this.value - this.minimumValue) / (this.maximumValue - this.minimumValue)))
        this.maximumTrackView.frame = CGRect(0.0, (this.bounds.height - 4.0) / 2.0, this.bounds.width, 4.0)
        this.minimumTrackView.frame = CGRect(0.0, (this.bounds.height - 4.0) / 2.0, this.bounds.width * progress, 4.0)
        this.thumbOutLightView.frame = CGRect(-7.5 + this.bounds.width * progress, (this.bounds.height - 15.0) / 2.0, 15.0, 15.0)
        this.thumbOutLightView.layer.cornerRadius = 7.5
        this.thumbView.frame = CGRect(-7.5 + this.bounds.width * progress, (this.bounds.height - 15.0) / 2.0, 15.0, 15.0)
        this.thumbView.layer.cornerRadius = 7.5
    }

    override fun pointInside(point: CGPoint): Boolean {
        return point.x >= -22.0 && point.y >= -(44.0 - this.frame.height) / 2.0 && point.x <= this.frame.width + 22.0 && point.y <= this.frame.height + (44.0 - this.frame.height) / 2.0
    }

}

fun KIMIPackage.installUISlider() {
    exporter.exportClass(UISlider::class.java, "UISlider", "UIView")
    exporter.exportProperty(UISlider::class.java, "value")
    exporter.exportProperty(UISlider::class.java, "minimumValue")
    exporter.exportProperty(UISlider::class.java, "maximumValue")
    exporter.exportProperty(UISlider::class.java, "minimumTrackTintColor")
    exporter.exportProperty(UISlider::class.java, "maximumTrackTintColor")
    exporter.exportProperty(UISlider::class.java, "thumbTintColor")
    exporter.exportMethodToJavaScript(UISlider::class.java, "edo_setValue")
}