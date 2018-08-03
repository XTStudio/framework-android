package com.xt.kimi.uikit

import com.xt.endo.CGRect

internal class UICollectionViewData(val collectionView: UICollectionView, layout: UICollectionViewLayout) {

    fun validateLayoutInRect(rect: CGRect) {

    }

    fun rectForItemAtIndexPath(indexPath: UIIndexPath): CGRect {
        return CGRect(0.0, 0.0, 0.0, 0.0)
    }

    fun globalIndexForItemAtIndexPath(indexPath: UIIndexPath): Int {
        return 0
    }

    fun indexPathForItemAtGlobalIndex(index: Int): UIIndexPath? {
        return null
    }

    fun layoutAttributesForElementsInRect(rect: CGRect): List<UICollectionViewLayoutAttributes> {
        return listOf()
    }

    fun invalidate() {

    }

    fun numberOfItemsBeforeSection(section: Int): Int {
        return 0
    }

    fun numberOfItemsInSection(section: Int): Int {
        return 0
    }

    fun numberOfItems(): Int {
        return 0
    }

    fun numberOfSections(): Int {
        return 0
    }

    fun collectionViewContentRect(): CGRect {
        return CGRect(0.0, 0.0, 0.0, 0.0)
    }

    var layoutIsPrepared: Boolean = false
        private set

}