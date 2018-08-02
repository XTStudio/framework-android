package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.kimi.KIMIPackage
import java.util.*

enum class UILayoutConstraintAxis {
    horizontal,
    vertical
}

enum class UIStackViewDistribution {
    fill,
    fillEqually,
    fillProportionally,
    equalSpacing,
    equalCentering
}

enum class UIStackViewAlignment {
    fill,
    leading,
    center,
    trailing
}

class UIStackView(arrangedSubviews: List<UIView>?): UIView() {

    var arrangedSubviews: List<UIView> = listOf()
        private set (value) {
            field = value
        }

    init {
        this.arrangedSubviews = arrangedSubviews ?: listOf()
        this.arrangedSubviews.forEach {
            this.addSubview(it)
        }
        post {
            this._layoutArrangeSubviews()
        }
    }

    fun addArrangedSubview(view: UIView) {
        this.arrangedSubviews = kotlin.run {
            val arrangedSubviews = this.arrangedSubviews.toMutableList()
            arrangedSubviews.add(view)
            return@run arrangedSubviews.toList()
        }
        this.addSubview(view)
        this._layoutArrangeSubviews()
    }

    fun removeArrangedSubview(view: UIView) {
        if (this.arrangedSubviews.contains(view)) {
            this.arrangedSubviews = kotlin.run {
                val arrangedSubviews = this.arrangedSubviews.toMutableList()
                arrangedSubviews.remove(view)
                return@run arrangedSubviews.toList()
            }
            view.removeFromSuperview()
            this._layoutArrangeSubviews()
        }
    }

    fun insertArrangedSubview(view: UIView, atIndex: Int) {
        this.arrangedSubviews = kotlin.run {
            val arrangedSubviews = this.arrangedSubviews.toMutableList()
            arrangedSubviews.add(atIndex, view)
            return@run arrangedSubviews.toList()
        }
        this.addSubview(view)
        this._layoutArrangeSubviews()
    }

    fun layoutArrangedSubview(subview: UIView, size: Map<String, Any>?) {
        this.allLayoutWidths.remove(subview)
        this.allLayoutHeights.remove(subview)
        size?.let { size ->
            (size["width"] as? Number)?.let {
                this.allLayoutWidths[subview] = it.toDouble()
            }
            (size["height"] as? Number)?.let {
                this.allLayoutHeights[subview] = it.toDouble()
            }
        }
        this._layoutArrangeSubviews()
    }

    var axis: UILayoutConstraintAxis = UILayoutConstraintAxis.horizontal
        set(value) {
            field = value
            this._layoutArrangeSubviews()
        }

    var distribution: UIStackViewDistribution = UIStackViewDistribution.fill
        set(value) {
            field = value
            this._layoutArrangeSubviews()
        }

    var alignment: UIStackViewAlignment = UIStackViewAlignment.fill
        set(value) {
            field = value
            this._layoutArrangeSubviews()
        }

    var spacing: Double = 0.0
        set(value) {
            field = value
            this._layoutArrangeSubviews()
        }

    // Implementation

    private val allLayoutWidths: WeakHashMap<UIView, Double> = WeakHashMap()

    private val allLayoutHeights: WeakHashMap<UIView, Double> = WeakHashMap()

    override fun layoutSubviews() {
        super.layoutSubviews()
        this._layoutArrangeSubviews()
    }

    private fun _layoutArrangeSubviews() {
        if (this.arrangedSubviews.count() == 0) { return }
        val x: MutableList<Double> = mutableListOf()
        val y: MutableList<Double> = mutableListOf()
        val width: MutableList<Double> = mutableListOf()
        val height: MutableList<Double> = mutableListOf()
        val axisLocation: MutableList<Double>
        val axisLength: MutableList<Double>
        val alignLocation: MutableList<Double>
        val alignLength: MutableList<Double>
        val axisValues: WeakHashMap<UIView, Double>
        val alignValues: WeakHashMap<UIView, Double>
        val boundsAxisLength: Double
        val boundsAlignLength: Double
        if (this.axis == UILayoutConstraintAxis.horizontal) {
            axisLocation = x
            axisLength = width
            alignLocation = y
            alignLength = height
            axisValues = allLayoutWidths
            alignValues = allLayoutHeights
            boundsAxisLength = this.bounds.width
            boundsAlignLength = this.bounds.height
        }
        else {
            axisLocation = y
            axisLength = height
            alignLocation = x
            alignLength = width
            axisValues = allLayoutHeights
            alignValues = allLayoutWidths
            boundsAxisLength = this.bounds.height
            boundsAlignLength = this.bounds.width
        }
        when (this.distribution) {
            UIStackViewDistribution.fill -> {
                var leftSpace = boundsAxisLength - axisValues.values.sum()
                var location = 0.0
                this.arrangedSubviews.forEachIndexed { index, view ->
                    var space = 0.0
                    axisValues[view]?.let {
                        space = it
                    } ?: kotlin.run {
                        if (this.arrangedSubviews.count() < 2 || index == this.arrangedSubviews.count() - 2) {
                            space = leftSpace
                            leftSpace = 0.0
                        }
                    }
                    axisLocation.add(location)
                    axisLength.add(space)
                    location += space
                }
            }
            UIStackViewDistribution.fillEqually -> {
                this.arrangedSubviews.forEachIndexed { index, _ ->
                    axisLocation.add(boundsAxisLength / this.arrangedSubviews.count() * index)
                    axisLength.add(boundsAxisLength / this.arrangedSubviews.count())
                }
            }
            UIStackViewDistribution.fillProportionally -> {
                if (this.arrangedSubviews.count() == 1) {
                    axisLocation.add(0.0)
                    axisLength.add(this.bounds.width)
                }
                else {
                    val everyWidth = (boundsAxisLength - ((this.arrangedSubviews.count() - 1) * spacing)) / this.arrangedSubviews.count()
                    this.arrangedSubviews.forEachIndexed { index, _ ->
                        axisLocation.add((everyWidth + this.spacing) * index)
                        axisLength.add(everyWidth)
                    }
                }
            }
            UIStackViewDistribution.equalSpacing -> {
                if (this.arrangedSubviews.count() == 1) {
                    axisLocation.add(0.0)
                    axisLength.add(boundsAxisLength)
                }
                else {
                    val spacing = (boundsAxisLength - axisValues.values.sum()) / (this.arrangedSubviews.count() - 1)
                    var location = 0.0
                    this.arrangedSubviews.forEach { view ->
                        axisLocation.add(location)
                        var space = axisValues[view] ?: 0.0
                        axisLength.add(space)
                        location += space + spacing
                    }
                }
            }
            UIStackViewDistribution.equalCentering -> {
                if (this.arrangedSubviews.count() > 2) {
                    val firstViewCenterX = (axisValues[this.arrangedSubviews[0]] ?: 0.0) / 2.0
                    val lastViewCenterX = boundsAxisLength - (axisValues[this.arrangedSubviews.last()] ?: 0.0) / 2.0
                    val everyCenterSpace = (lastViewCenterX - firstViewCenterX) / (this.arrangedSubviews.count() - 1)
                    var location = 0.0
                    this.arrangedSubviews.forEachIndexed { index, it ->
                        var space = axisValues[it] ?: 0.0
                        axisLength.add(space)
                        if (index > 0) {
                            location -= space / 2.0
                        }
                        axisLocation.add(location)
                        location += space / 2.0 + everyCenterSpace
                    }
                }
                else if (this.arrangedSubviews.count() == 2) {
                    var leftSpace = boundsAxisLength
                    var location = 0.0
                    axisLocation.add(location)
                    val firstSpace = axisValues[this.arrangedSubviews[0]] ?: 0.0
                    axisLength.add(firstSpace)
                    leftSpace -= firstSpace
                    location += firstSpace
                    val secondSpace = axisValues[this.arrangedSubviews[1]] ?: leftSpace
                    axisLocation.add(boundsAxisLength - secondSpace)
                    axisLength.add(secondSpace)
                }
                else if (this.arrangedSubviews.count() == 1) {
                    axisLocation.add(0.0)
                    axisLength.add(boundsAxisLength)
                }
            }
        }
        when (this.alignment) {
            UIStackViewAlignment.fill -> {
                this.arrangedSubviews.forEach {
                    alignLocation.add(0.0)
                    alignLength.add(boundsAlignLength)
                }
            }
            UIStackViewAlignment.leading -> {
                this.arrangedSubviews.forEach {
                    alignLocation.add(0.0)
                    alignLength.add(alignValues[it] ?: 0.0)
                }
            }
            UIStackViewAlignment.center -> {
                this.arrangedSubviews.forEach {
                    val space = alignValues[it] ?: 0.0
                    alignLocation.add((boundsAlignLength - space) / 2.0)
                    alignLength.add(space)
                }
            }
            UIStackViewAlignment.trailing -> {
                this.arrangedSubviews.forEach {
                    val space = alignValues[it] ?: 0.0
                    alignLocation.add(boundsAlignLength - space)
                    alignLength.add(space)
                }
            }
        }
        if (this.axis == UILayoutConstraintAxis.horizontal) {
            this.arrangedSubviews.forEachIndexed { index, view ->
                view.frame = CGRect(axisLocation[index], alignLocation[index], axisLength[index], alignLength[index])
            }
        }
        else {
            this.arrangedSubviews.forEachIndexed { index, view ->
                view.frame = CGRect(alignLocation[index], axisLocation[index], alignLength[index], axisLength[index])
            }
        }
    }

}

fun KIMIPackage.installUIStackView() {
    exporter.exportClass(UIStackView::class.java, "UIStackView", "UIView")
    exporter.exportProperty(UIStackView::class.java, "arrangedSubviews", true)
    exporter.exportMethodToJavaScript(UIStackView::class.java, "addArrangedSubview")
    exporter.exportMethodToJavaScript(UIStackView::class.java, "removeArrangedSubview")
    exporter.exportMethodToJavaScript(UIStackView::class.java, "insertArrangedSubview")
    exporter.exportMethodToJavaScript(UIStackView::class.java, "layoutArrangedSubview")
    exporter.exportProperty(UIStackView::class.java, "axis")
    exporter.exportProperty(UIStackView::class.java, "distribution")
    exporter.exportProperty(UIStackView::class.java, "alignment")
    exporter.exportProperty(UIStackView::class.java, "spacing")
    exporter.exportInitializer(UIStackView::class.java) {
        return@exportInitializer UIStackView(it.firstOrNull() as? List<UIView>)
    }
    exporter.exportEnum("UILayoutConstraintAxis", mapOf(
            Pair("horizontal", UILayoutConstraintAxis.horizontal),
            Pair("vertical", UILayoutConstraintAxis.vertical)
    ))
    exporter.exportEnum("UIStackViewDistribution", mapOf(
            Pair("fill", UIStackViewDistribution.fill),
            Pair("fillEqually", UIStackViewDistribution.fillEqually),
            Pair("fillProportionally", UIStackViewDistribution.fillProportionally),
            Pair("equalSpacing", UIStackViewDistribution.equalSpacing),
            Pair("equalCentering", UIStackViewDistribution.equalCentering)
    ))
    exporter.exportEnum("UIStackViewAlignment", mapOf(
            Pair("fill", UIStackViewAlignment.fill),
            Pair("leading", UIStackViewAlignment.leading),
            Pair("center", UIStackViewAlignment.center),
            Pair("trailing", UIStackViewAlignment.trailing)
    ))
}