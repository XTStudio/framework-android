package com.xt.kimi.uikit

import com.xt.endo.*
import com.xt.kimi.KIMIPackage

enum class UIButtonType {
    custom,
    system,
}

enum class UIControlContentVerticalAlignment {
    center,
    top,
    bottom,
    fill,
}

enum class UIControlContentHorizontalAlignment {
    center,
    left,
    right,
    fill,
}

enum class UIControlState(val rawValue: Int) {
    normal(0),
    highlighted(1 shl 0),
    disabled(1 shl 1),
    selected(1 shl 2),
}

open class UIButton(val buttonType: UIButtonType): UIView() {

    val titleLabel: UILabel

    val imageView: UIImageView

    override var frame: CGRect
        get() = super.frame
        set(value) { super.frame = value; this.reloadContents(); }

    var edo_enabled = true
        set(value) {
            field = value
            this.reloadContents()
        }

    var edo_selected = false
        set(value) {
            field = value
            this.reloadContents()
        }

    var highlighted = false
        private set(value) {
            if (field == value) { return }
            field = value
            this.reloadContents()
        }

    var tracking = false
        private set(value) {
            field = value
            this.reloadContents()
        }

    var touchInside = false
        private set(value) {
            if (field == value) { return }
            field = value
            this.reloadContents()
        }

    var contentVerticalAlignment = UIControlContentVerticalAlignment.center
        set(value) {
            field = value
            this.reloadContents()
        }

    var contentHorizontalAlignment = UIControlContentHorizontalAlignment.center
        set(value) {
            field = value
            this.reloadContents()
        }

    fun setTitle(title: String?, state: Int) {
        title?.let { this.statedTitles[state] = it } ?: kotlin.run { this.statedTitles.remove(state) }
        this.reloadContents()
    }

    fun setTitleColor(color: UIColor?, state: Int) {
        color?.let { this.statedTitleColors[state] = it } ?: kotlin.run { this.statedTitleColors.remove(state) }
        this.reloadContents()
    }

    fun setTitleFont(font: UIFont) {
        this.titleLabel.font = font
        this.reloadContents()
    }

    fun setImage(image: UIImage?, state: Int) {
        image?.let { this.statedImages[state] = image } ?: kotlin.run { this.statedImages.remove(state) }
        this.reloadContents()
    }

    fun setAttributedTitle(title: UIAttributedString?, state: Int) {
        title?.let { this.statedTitles[state] = it } ?: kotlin.run { this.statedTitles.remove(state) }
        this.reloadContents()
    }

    var contentEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.reloadContents()
        }

    var titleEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.reloadContents()
        }

    var imageEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.reloadContents()
        }

    // Implementation

    private val statedTitles: MutableMap<Int, Any> = mutableMapOf()
    private val statedTitleColors: MutableMap<Int, UIColor> = mutableMapOf()
    private val statedImages: MutableMap<Int, UIImage> = mutableMapOf()

    init {
        this.titleLabel = UILabel()
        this.titleLabel.font = UIFont(17.0)
        this.imageView = UIImageView()
        this.addSubview(this.titleLabel)
        this.addSubview(this.imageView)
        this.setupTouches()
    }

    private fun setupTouches() {
        this.addGestureRecognizer(object : UITapGestureRecognizer() {
            override fun handleEvent(name: String) {
                super.handleEvent(name)
                if (name == "touch") {
                    this@UIButton.sendEvent("touchUpInside")
                }
            }
        })
        val longPressGesture = object : UILongPressGestureRecognizer(){
            override fun handleEvent(name: String) {
                super.handleEvent(name)
                if (name == "began") {
                    this@UIButton.tracking = true
                    this@UIButton.highlighted = true
                    this@UIButton.sendEvent("touchDown")
                }
                else if (name == "changed") {
                    val location = this.locationInView(null)
                    val inside = this@UIButton.highlightedPointInside(location)
                    if (this@UIButton.touchInside != inside) {
                        if (inside) {
                            this@UIButton.sendEvent("touchDragEnter")
                        }
                        else {
                            this@UIButton.sendEvent("touchDragExit")
                        }
                    }
                    this@UIButton.touchInside = inside
                    this@UIButton.highlighted = this@UIButton.touchInside
                    if (inside) {
                        this@UIButton.sendEvent("touchDragInside")
                    }
                    else {
                        this@UIButton.sendEvent("touchDragOutside")
                    }
                }
                else if (name == "ended") {
                    this@UIButton.highlighted = false
                    this@UIButton.tracking = false
                    val location = this.locationInView(null)
                    val inside = this@UIButton.highlightedPointInside(location)
                    if (inside) {
                        this@UIButton.sendEvent("touchUpInside")
                    }
                    else {
                        this@UIButton.sendEvent("touchUpOutside")
                    }
                }
                else if (name == "cancelled") {
                    this@UIButton.highlighted = false
                    this@UIButton.tracking = false
                    this@UIButton.sendEvent("touchCancel")
                }
            }
        }
        longPressGesture.minimumPressDuration = 0.05
        this.addGestureRecognizer(longPressGesture)
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        if (this.buttonType == UIButtonType.system) {
            this.tintColor?.let { tintColor ->
                this.setTitleColor(tintColor, UIControlState.normal.rawValue)
                this.setTitleColor(UIColor.gray.colorWithAlphaComponent(0.75), UIControlState.disabled.rawValue)
            }
        }
    }

    protected open fun sendEvent(name: String) {
        EDOJavaHelper.emit(this, name, this)
    }

    private fun reloadContents() {
        val title = this.titleForState(this.currentState())
        kotlin.run {
            (title as? String)?.let {
                this.titleLabel.text = it
                return@run
            }
            (title as? UIAttributedString)?.let {
                this.titleLabel.attributedText = it
                return@run
            }
            this.titleLabel.text = null
            this.titleLabel.attributedText = null
        }
        this.titleLabel.textColor = this.titleColorForState(this.currentState())
        this.imageView.image = this.imageForState(this.currentState())
        if (this.buttonType == UIButtonType.system) {
            UIAnimator.shared.linear(0.10, EDOCallback.createWithBlock {
                if (this.highlighted) {
                    this.titleLabel.edo_alpha = 0.3
                    this.imageView.edo_alpha = 0.3
                }
                else {
                    this.titleLabel.edo_alpha = 1.0
                    this.imageView.edo_alpha = 1.0
                }
            }, null)
        }
        this.reloadLayouts()
    }

    private fun reloadLayouts() {
        val imageViewSize = this.imageView.intrinsicContentSize() ?: CGSize(0.0, 0.0)
        var imgX = 0.0
        var imgY = 0.0
        var imgWidth = imageViewSize.width
        var imgHeight = imageViewSize.height
        val titleLabelSize = this.titleLabel.intrinsicContentSize() ?: CGSize(0.0, 0.0)
        var titleX = 0.0
        var titleY = 0.0
        var titleWidth = titleLabelSize.width
        var titleHeight = titleLabelSize.height
        when (this.contentHorizontalAlignment) {
            UIControlContentHorizontalAlignment.left -> {
                imgX = 0.0
                titleX = imgX + imageViewSize.width + 0.0
            }
            UIControlContentHorizontalAlignment.center -> {
                imgX = (this.bounds.width - (imageViewSize.width + titleLabelSize.width)) / 2.0
                titleX = imgX + imageViewSize.width + 0.0
            }
            UIControlContentHorizontalAlignment.right -> {
                imgX = this.bounds.width - (imageViewSize.width + titleLabelSize.width)
                titleX = imgX + imageViewSize.width + 0.0
            }
            UIControlContentHorizontalAlignment.fill -> {
                imgWidth = this.bounds.width
                titleWidth = this.bounds.width
            }
        }
        when (this.contentVerticalAlignment) {
            UIControlContentVerticalAlignment.top -> {
                imgY = 0.0
                titleY = 0.0
            }
            UIControlContentVerticalAlignment.center -> {
                imgY = (this.bounds.height - imageViewSize.height) / 2.0
                titleY = (this.bounds.height - titleLabelSize.height) / 2.0
            }
            UIControlContentVerticalAlignment.bottom -> {
                imgY = this.bounds.height - imageViewSize.height
                titleY = this.bounds.height - titleLabelSize.height
            }
            UIControlContentVerticalAlignment.fill -> {
                imgHeight = this.bounds.height
                titleHeight = this.bounds.height
            }
        }
        imgX += contentEdgeInsets.left + imageEdgeInsets.left
        imgX -= contentEdgeInsets.right + imageEdgeInsets.right
        imgY += contentEdgeInsets.top + imageEdgeInsets.top
        imgY -= contentEdgeInsets.bottom + imageEdgeInsets.bottom
        titleX += contentEdgeInsets.left + titleEdgeInsets.left
        titleX -= contentEdgeInsets.right + titleEdgeInsets.right
        titleY += contentEdgeInsets.top + titleEdgeInsets.top
        titleY -= contentEdgeInsets.bottom + titleEdgeInsets.bottom
        this.imageView.frame = CGRect(imgX, imgY, imgWidth, imgHeight)
        this.titleLabel.frame = CGRect(titleX, titleY, titleWidth, titleHeight)
    }

    private fun currentState(): Int {
        var state = UIControlState.normal.rawValue
        if (!this.edo_enabled) {
            state = state or UIControlState.disabled.rawValue
        }
        if (this.edo_selected) {
            state = state or UIControlState.selected.rawValue
        }
        if (this.highlighted) {
            state = state or UIControlState.highlighted.rawValue
        }
        return state
    }

    private fun imageForState(state: Int): UIImage? {
        return this.statedImages[state] ?: this.statedImages[0]
    }

    private fun titleForState(state: Int): Any {
        return this.statedTitles[state] ?: this.statedTitles[0] ?: ""
    }

    private fun titleColorForState(state: Int): UIColor {
        return this.statedTitleColors[state] ?: this.statedTitleColors[0] ?: UIColor.black
    }

    private fun highlightedPointInside(point: CGPoint): Boolean {
        return point.x >= -22.0 && point.y >= -22.0 && point.x <= this.frame.width + 22.0 && point.y <= this.frame.height + 22.0
    }

}

fun KIMIPackage.installUIButton() {
    exporter.exportClass(UIButton::class.java, "UIButton", "UIView")
    exporter.exportMethodToJavaScript(UIButton::class.java, "setTitle")
    exporter.exportMethodToJavaScript(UIButton::class.java, "setImage")
    exporter.exportMethodToJavaScript(UIButton::class.java, "setAttributedTitle")
    exporter.exportMethodToJavaScript(UIButton::class.java, "setTitleFont")
    exporter.exportMethodToJavaScript(UIButton::class.java, "setTitleColor")
    exporter.exportProperty(UIButton::class.java, "contentEdgeInsets")
    exporter.exportProperty(UIButton::class.java, "titleEdgeInsets")
    exporter.exportProperty(UIButton::class.java, "imageEdgeInsets")
    exporter.exportProperty(UIButton::class.java, "edo_enabled")
    exporter.exportProperty(UIButton::class.java, "edo_selected")
    exporter.exportProperty(UIButton::class.java, "highlighted", true)
    exporter.exportProperty(UIButton::class.java, "tracking", true)
    exporter.exportProperty(UIButton::class.java, "touchInside", true)
    exporter.exportProperty(UIButton::class.java, "contentVerticalAlignment")
    exporter.exportProperty(UIButton::class.java, "contentHorizontalAlignment")
    exporter.exportInitializer(UIButton::class.java) {
        return@exportInitializer UIButton(if (it.firstOrNull() as? Boolean == true) UIButtonType.custom else UIButtonType.system)
    }
    exporter.exportEnum("UIControlContentVerticalAlignment", mapOf(
            Pair("normal", UIControlContentVerticalAlignment.center),
            Pair("top", UIControlContentVerticalAlignment.top),
            Pair("bottom", UIControlContentVerticalAlignment.bottom),
            Pair("fill", UIControlContentVerticalAlignment.fill)
    ))
    exporter.exportEnum("UIControlContentHorizontalAlignment", mapOf(
            Pair("center", UIControlContentHorizontalAlignment.center),
            Pair("left", UIControlContentHorizontalAlignment.left),
            Pair("right", UIControlContentHorizontalAlignment.right),
            Pair("fill", UIControlContentHorizontalAlignment.fill)
    ))
    exporter.exportEnum("UIControlState", mapOf(
            Pair("normal", UIControlState.normal.rawValue),
            Pair("highlighted", UIControlState.highlighted.rawValue),
            Pair("disabled", UIControlState.disabled.rawValue),
            Pair("selected", UIControlState.selected.rawValue)
    ))
}