package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UICollectionViewCell: UICollectionReusableView() {

    var edo_selected = false

    var edo_highlighted = false

    fun applyLayoutAttributes(layoutAttributes: UICollectionViewLayoutAttributes) {

    }

}

fun KIMIPackage.installUICollectionViewCell() {
    exporter.exportClass(UICollectionViewCell::class.java, "UICollectionViewCell", "UIView")
}