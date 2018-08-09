package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.UIEdgeInsets
import com.xt.kimi.KIMIPackage

class UITabBar: UIView() {

    var translucent: Boolean = false

    override var hidden: Boolean
        get() = super.hidden
        set(value) {
            super.hidden = value
            this.tabBarController?.view?.setNeedsLayout(true)
        }

    var barHeight: Double = 50.0

    var barTintColor: UIColor? = null
        set(value) {
            field = value
            this.edo_backgroundColor = value
        }

    var unselectedItemTintColor: UIColor = UIColor(0x73 / 255.0, 0x73 / 255.0, 0x73 / 255.0, 1.0)

    // Implementation

    internal var tabBarController: UITabBarController? = null

    private val shadowPaint = Paint()

    private var barButtons: List<UITabBarButton> = listOf()

    init {
        this.barTintColor = UIColor.white
        this.tintColor = UIColor.black
    }

    internal fun resetItems() {
        this.barButtons.forEach {
            it.removeFromSuperview()
            it.barItem?.barButton = null
            it.barItem = null
        }
        this.tabBarController?.let { tabBarController ->
            this.barButtons = tabBarController.childViewControllers.map {
                val tabBarButton = UITabBarButton()
                tabBarButton.barItem = it.tabBarItem
                it.tabBarItem.barButton = tabBarButton
                return@map tabBarButton
            }
            this.barButtons.forEachIndexed { index, it ->
                it.addGestureRecognizer(object : UITapGestureRecognizer() {
                    override fun handleEvent(name: String) {
                        super.handleEvent(name)
                        if (name == "touch") {
                            this@UITabBar.tabBarController?.selectedIndex = index
                        }
                    }
                })
                this.addSubview(it)
            }
        }
        this.setNeedsLayout(true)
    }

    internal fun setSelectedIndex(selectedIndex: Int) {
        this.barButtons.forEachIndexed { index, barButton ->
            barButton.itemSelected = index == selectedIndex
            barButton.tintColor = if (index == selectedIndex) this.tintColor else this.unselectedItemTintColor
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        if (this.barButtons.count() > 0) {
            val eachWidth = this.bounds.width / this.barButtons.count().toDouble()
            this.barButtons.forEachIndexed { index, barButton ->
                barButton.frame = CGRect(index.toDouble() * eachWidth, 0.0, eachWidth, this.bounds.height)
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val canvas = canvas ?: return
        shadowPaint.color = Color.rgb(0x98, 0x96, 0x9b)
        shadowPaint.strokeWidth = 1f
        shadowPaint.style = Paint.Style.STROKE
        canvas.drawLine(0.0f, 0.0f, canvas.width.toFloat(), 0.0f, shadowPaint)
    }

}

internal class UITabBarButton: UIView() {

    var barItem: UITabBarItem? = null
        set(value) {
            field = value
            this.setNeedUpdate()
        }

    var itemSelected = false
        set(value) {
            if (field == value) { return }
            field = value
            this.setNeedUpdate()
        }

    var iconImageView = UIImageView()

    var titleLabel = UILabel()

    init {
        this.addSubview(this.iconImageView)
        this.titleLabel.font = UIFont(11.0)
        this.addSubview(this.titleLabel)
    }

    fun setNeedUpdate() {
        this.iconImageView.image = if (this.itemSelected) this.barItem?.selectedImage ?: this.barItem?.image else this.barItem?.image
        this.titleLabel.text = this.barItem?.title
        this.setNeedsLayout(true)
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        this.titleLabel.textColor = this.tintColor
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        val iconSize = this.iconImageView.intrinsicContentSize() ?: CGSize(0.0, 0.0)
        val titleSize = this.titleLabel.intrinsicContentSize() ?: CGSize(0.0, 0.0)
        val imageInsets = this.barItem?.imageInsets ?: UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        this.iconImageView.frame = CGRect(
                imageInsets.left + (this.bounds.width - iconSize.width) / 2.0 - imageInsets.right,
                imageInsets.top + (this.bounds.height - (iconSize.height + 4.0 + titleSize.height)) / 2.0,
                iconSize.width,
                iconSize.height
        )
        this.titleLabel.frame = CGRect(
                (this.bounds.width - titleSize.width) / 2.0,
                this.iconImageView.frame.y + this.iconImageView.frame.height + 4.0 + imageInsets.bottom,
                titleSize.width,
                titleSize.height
        )
    }

}

fun KIMIPackage.installUITabBar() {
    exporter.exportClass(UITabBar::class.java, "UITabBar", "UIView")
    exporter.exportProperty(UITabBar::class.java, "translucent")
    exporter.exportProperty(UITabBar::class.java, "barTintColor")
    exporter.exportProperty(UITabBar::class.java, "unselectedItemTintColor")

}