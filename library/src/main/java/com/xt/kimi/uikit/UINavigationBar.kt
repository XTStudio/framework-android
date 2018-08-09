package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.xt.endo.CGAffineTransform
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import kotlin.math.exp

class UINavigationBar: UIView() {

    internal var navigationController: UINavigationController? = null

    override var hidden: Boolean
        get() = super.hidden
        set(value) {
            super.hidden = value
            this.navigationController?.view?.setNeedsLayout(true)
        }

    val barHeight = 48.0

    var translucent: Boolean = false

    var barTintColor: UIColor? = null
        set(value) {
            field = value
            this.edo_backgroundColor = value
        }

    var titleTextAttributes: Map<String, Any> = mapOf()

    var backIndicatorImage: UIImage? = UIImage.fromBase64("iVBORw0KGgoAAAANSUhEUgAAADkAAAA8CAMAAADrC+IEAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABOUExURUdwTP///////////////////////////////////////////////////////////////////////////////////////////////////4il91oAAAAZdFJOUwD+ZRPxOKUhoAPR0BY3pxSZmAIOMyhEh7SA0BQ9AAAAfElEQVRIx+3VSQqAMBAF0XaIGudZ+/4XVaMLCQjxo4LQtX/rIrpO14qgdMShAiFD1ECEHnClPQg5CwQ+D1OBAv8Cq68hz4ljnQXdy1FoZMOoLEJUYtRIiO7yRKfYsZYs6vl3z6WEChUq1JGOKC01YRSAO4XgRkFINKDwrRZIeEfaMx4tYAAAAABJRU5ErkJggg==", 3, UIImageRenderingMode.alwaysTemplate)

    var backIndicatorTransitionMaskImage: UIImage? = null

    private val shadowPaint = Paint()

    init {
        this.barTintColor = UIColor.white
        this.tintColor = UIColor.black
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val canvas = canvas ?: return
        shadowPaint.color = Color.rgb(0x98, 0x96, 0x9b)
        shadowPaint.strokeWidth = 1f
        shadowPaint.style = Paint.Style.STROKE
        canvas.drawLine(0.0f, canvas.height.toFloat() - 1f, canvas.width.toFloat(), canvas.height.toFloat() - 1f, shadowPaint)
    }

    // Implementation

    val topItem: UINavigationItem?
        get() {
            return this.items.lastOrNull()
        }

    val backItem: UINavigationItem?
        get() {
            return this.items.getOrNull(this.items.count() - 2)
        }

    var items: List<UINavigationItem> = listOf()
        private set

    fun setItems(items: List<UINavigationItem>, animated: Boolean) {
        this.items.forEach {
            it.allViews().forEach { it.removeFromSuperview() }
        }
        this.items = items
        this.displayItems()
    }

    fun pushNavigationItem(item: UINavigationItem, animated: Boolean) {
        this.items = kotlin.run {
            val items = this.items.toMutableList()
            items.add(item)
            return@run items
        }
        item.navigationBar = this
        this.displayItems()
        if (animated) {
            kotlin.run {
                val fromItem = this.items.getOrNull(this.items.count() - 2) ?: return@run
                val toItem = this.items.getOrNull(this.items.count() - 1) ?: return@run
                fromItem.allViews().forEach {
                    it.edo_alpha = 1.0
                }
                toItem.allViews().forEach {
                    it.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, it.bounds.width, 0.0)
                    it.edo_alpha = 1.0
                }
                UIAnimator.shared.bouncy(0.0, 16.0, EDOCallback.createWithBlock {
                    fromItem.allViews().forEach {
                        it.edo_alpha = 0.0
                    }
                    toItem.allViews().forEach {
                        it.transform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                        it.edo_alpha = 1.0
                    }
                }, null)
            }
        }
    }

    fun popNavigationItem(animated: Boolean) {
        if (this.items.count() <= 1) { return }
        val fromItem = items.getOrNull(this.items.count() - 1) ?: return
        val toItem = items.getOrNull(this.items.count() - 2) ?: return
        this.items = kotlin.run {
            val items = this.items.toMutableList()
            items.removeAt(this.items.count() - 1)
            return@run items
        }
        if (animated) {
            fromItem.allViews().forEach {
                it.edo_alpha = 1.0
            }
            toItem.allViews().forEach {
                it.edo_alpha = 0.0
            }
            UIAnimator.shared.bouncy(0.0, 16.0, EDOCallback.createWithBlock {
                fromItem.allViews().forEach {
                    it.edo_alpha = 0.0
                }
                toItem.allViews().forEach {
                    it.edo_alpha = 1.0
                }
            }, EDOCallback.createWithBlock {
                fromItem.allViews().forEach { it.removeFromSuperview() }
                this.displayItems()
            })
        }
        else {
            fromItem.allViews().forEach { it.removeFromSuperview() }
            this.displayItems()
        }
    }

    fun popToNavigationItem(item: UINavigationItem, animated: Boolean) {
        if (!this.items.contains(item)) { return }
        val targetIndex = this.items.indexOf(item)
        val fromItems = this.items.filterIndexed { index, _ -> index > targetIndex }
        if (fromItems.count() == 0) { return }
        this.items = this.items.filterIndexed { index, _ -> index <= targetIndex }
        val toItem = item
        if (animated) {
            val fromItem = fromItems.last()
            fromItem.allViews().forEach {
                it.edo_alpha = 1.0
            }
            toItem.allViews().forEach {
                it.edo_alpha = 0.0
            }
            UIAnimator.shared.bouncy(0.0, 16.0, EDOCallback.createWithBlock {
                fromItem.allViews().forEach {
                    it.edo_alpha = 0.0
                }
                toItem.allViews().forEach {
                    it.edo_alpha = 1.0
                }
            }, EDOCallback.createWithBlock {
                fromItems.forEach {
                    it.allViews().forEach {
                        it.removeFromSuperview()
                    }
                }
                this.displayItems()
            })
        }
        else {
            fromItems.forEach {
                it.allViews().forEach {
                    it.removeFromSuperview()
                }
            }
            this.displayItems()
        }
    }

    internal fun displayItems() {
        this.items.forEachIndexed { index, it ->
            if (it.titleView.superview != this) {
                this.addSubview(it.titleView)
            }
            if (it.backButton.superview != this) {
                this.addSubview(it.backButton)
            }
            it.leftViews().forEach {
                if (it.superview != this) {
                    this.addSubview(it)
                }
            }
            it.rightViews().forEach {
                if (it.superview != this) {
                    this.addSubview(it)
                }
            }
            it.backButton.hidden = it.hidesBackButton || index == 0 || it.leftBarButtonItems.count() > 0
            it.backButton.setImage(this.backIndicatorImage, UIControlState.normal.rawValue)
        }
        this.layoutItems()
    }

    private fun layoutItems() {
        val lastItemIndex = this.items.count() - 1
        this.items.forEachIndexed { index, item ->
            var leftX = 16.0
            if (!item.backButton.hidden) {
                item.backButton.frame = CGRect(0.0, this.bounds.height - barHeight, 44.0, barHeight)
                leftX += 32.0
            }
            kotlin.run {
                item.leftBarButtonItems.forEach { barButtonItem ->
                    barButtonItem.customView?.let {
                        it.frame = CGRect(leftX, this.bounds.height - barHeight, barButtonItem.width, barHeight)
                        leftX += barButtonItem.width + 8.0
                    }
                }
            }
            val titleViewSize = item.titleView.intrinsicContentSize() ?: CGSize(0.0, 0.0)
            item.titleView.frame = CGRect(leftX, (this.bounds.height - barHeight) + ((barHeight - titleViewSize.height) / 2.0), titleViewSize.width, titleViewSize.height)
            kotlin.run {
                var x = this.bounds.width - 16.0
                item.rightBarButtonItems.forEach { barButtonItem ->
                    barButtonItem.customView?.let {
                        x -= barButtonItem.width
                        it.frame = CGRect(x, this.bounds.height - barHeight, barButtonItem.width, barHeight)
                        x -= 8.0
                    }
                }
            }
            item.allViews().forEach {
                it.edo_alpha = if (index < lastItemIndex) 0.0 else 1.0
            }
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.layoutItems()
    }

}

fun KIMIPackage.installUINavigationBar() {
    exporter.exportClass(UINavigationBar::class.java, "UINavigationBar", "UIView")
    exporter.exportProperty(UINavigationBar::class.java, "translucent")
    exporter.exportProperty(UINavigationBar::class.java, "barTintColor")
    exporter.exportProperty(UINavigationBar::class.java, "titleTextAttributes")
    exporter.exportProperty(UINavigationBar::class.java, "backIndicatorImage")
    exporter.exportProperty(UINavigationBar::class.java, "backIndicatorTransitionMaskImage")
}