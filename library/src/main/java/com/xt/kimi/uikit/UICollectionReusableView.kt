package com.xt.kimi.uikit

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
            this.hidden = layoutAttributes.hidden
            this.transform = layoutAttributes.transform
            this.edo_alpha = layoutAttributes.alpha
        }
    }

}