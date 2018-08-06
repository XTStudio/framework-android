package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

open class UICollectionReusableView: UIView() {

    var collectionView: UICollectionView? = null

    var layoutAttributes: UICollectionViewLayoutAttributes? = null
        internal set

    var reuseIdentifier: String? = null
        internal set

    open fun prepareForReuse() {
        this.layoutAttributes = null
    }

    open fun applyLayoutAttributes(layoutAttributes: UICollectionViewLayoutAttributes) {
        if (layoutAttributes != this.layoutAttributes) {
            this.layoutAttributes = layoutAttributes
            this.frame = layoutAttributes.frame
            this.transform = layoutAttributes.transform
            // we will ignore following properties
//            this.hidden = layoutAttributes.hidden
//            this.edo_alpha = layoutAttributes.alpha
        }
    }

}

fun KIMIPackage.installUICollectionReusableView() {
    exporter.exportClass(UICollectionReusableView::class.java, "UICollectionReusableView", "UIView")
    exporter.exportProperty(UICollectionReusableView::class.java, "collectionView", true)
    exporter.exportProperty(UICollectionReusableView::class.java, "reuseIdentifier", true)
}