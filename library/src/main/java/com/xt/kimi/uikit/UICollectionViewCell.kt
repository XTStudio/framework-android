package com.xt.kimi.uikit

import com.xt.endo.EDOJavaHelper
import com.xt.kimi.KIMIPackage

class UICollectionViewCell: UICollectionReusableView() {

    var edo_selected = false
        set(value) {
            if (field == value) { return }
            field = value
            EDOJavaHelper.valueChanged(this, "selected")
            EDOJavaHelper.emit(this, "selected", this, value)
        }

    var edo_highlighted = false
        set(value) {
            if (field == value) { return }
            field = value
            EDOJavaHelper.valueChanged(this, "highlighted")
            EDOJavaHelper.emit(this, "highlighted", this, value)
        }

    val contentView: UIView = UIView()

    internal var currentIndexPath: UIIndexPath? = null

    override fun prepareForReuse() {
        super.prepareForReuse()
        this.edo_selected = false
        this.edo_highlighted = false
    }

    init {
        this.contentView.isImportantNodeForRendering = true
        addSubview(this.contentView)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.contentView.frame = this.bounds
    }

}

fun KIMIPackage.installUICollectionViewCell() {
    exporter.exportClass(UICollectionViewCell::class.java, "UICollectionViewCell", "UICollectionReusableView")
    exporter.exportProperty(UICollectionViewCell::class.java, "contentView", true, true)
    exporter.exportProperty(UICollectionViewCell::class.java, "edo_selected", true, true)
    exporter.exportProperty(UICollectionViewCell::class.java, "edo_highlighted", true, true)
}