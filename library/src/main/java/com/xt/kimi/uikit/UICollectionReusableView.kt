package com.xt.kimi.uikit

open class UICollectionReusableView: UIView() {

    var collectionView: UICollectionView? = null

    var layoutAttributes: UICollectionViewLayoutAttributes? = null
        internal set

    var reuseIdentifier: String? = null
        internal set

    fun prepareForReuse() {

    }

    fun applyLayoutAttributes(layoutAttributes: UICollectionViewLayoutAttributes) {

    }

}