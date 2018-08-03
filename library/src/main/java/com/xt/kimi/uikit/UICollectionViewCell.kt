package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UICollectionViewCell: UICollectionReusableView() {

    var edo_selected = false

    var edo_highlighted = false

    val contentView: UIView = UIView()

    override fun prepareForReuse() {
        super.prepareForReuse()
        this.edo_selected = false
        this.edo_highlighted = false
    }

    init {
        addSubview(this.contentView)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.contentView.frame = this.bounds
    }

}

fun KIMIPackage.installUICollectionViewCell() {
    exporter.exportClass(UICollectionViewCell::class.java, "UICollectionViewCell", "UIView")
}