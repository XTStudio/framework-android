package com.xt.kimi.uikit

import com.xt.endo.CGRect
import com.xt.endo.CGSize

internal class UICollectionViewData(val collectionView: UICollectionView, val layout: UICollectionViewLayout) {

    fun validateLayoutInRect(rect: CGRect) {
        this.validateItemCounts()
        this.prepareToLoadData()
        if (this.validLayoutRect == null || !CGRectEqualToRect(this.validLayoutRect!!, rect)) {
            this.validLayoutRect = rect
            this.cachedLayoutAttributes = this.layout.layoutAttributesForElementsInRect(rect).filter {
                return@filter it.isCell() || it.isDecorationView() || it.isSupplementaryView()
            }
        }
    }

    fun rectForItemAtIndexPath(indexPath: UIIndexPath): CGRect {
        return CGRect(0.0, 0.0, 0.0, 0.0)
    }

    fun globalIndexForItemAtIndexPath(indexPath: UIIndexPath): Int {
        return this.numberOfItemsBeforeSection(indexPath.section) + indexPath.row
    }

    fun indexPathForItemAtGlobalIndex(index: Int): UIIndexPath? {
        this.validateItemCounts()
        var section = 0
        var countItems = 0
        for (i in 0 until _numSections) {
            section = i
            val countIncludingThisSection = countItems + _sectionItemCounts[section]
            if (countIncludingThisSection > index) {
                break
            }
            countItems = countIncludingThisSection
        }
        val item = index - countItems
        return UIIndexPath(item, section)
    }

    fun layoutAttributesForElementsInRect(rect: CGRect): List<UICollectionViewLayoutAttributes> {
        this.validateLayoutInRect(rect)
        return this.cachedLayoutAttributes
    }

    fun invalidate() {
        this.itemCountsAreValid = false
        this.layoutIsPrepared = false
        this.validLayoutRect = null
    }

    fun numberOfItemsBeforeSection(section: Int): Int {
        this.validateItemCounts()
        var returnCount = 0
        for (i in 0 until section) {
            returnCount += _sectionItemCounts[i]
        }
        return returnCount
    }

    fun numberOfItemsInSection(section: Int): Int {
        this.validateItemCounts()
        if (section >= _numSections || section < 0) {
            return 0
        }
        return _sectionItemCounts[section]
    }

    fun numberOfItems(): Int {
        this.validateItemCounts()
        return this._numItems
    }

    fun numberOfSections(): Int {
        this.validateItemCounts()
        return _numSections
    }

    fun collectionViewContentRect(): CGRect {
        return CGRect(0.0, 0.0, this.contentSize.width, this.contentSize.height)
    }

    var layoutIsPrepared: Boolean = false
        private set

    private var itemCountsAreValid = false

    private var _numSections = 0

    private var _numItems = 0

    private var _sectionItemCounts: List<Int> = listOf()

    private var validLayoutRect: CGRect? = null

    private var contentSize: CGSize = CGSize(0.0, 0.0)

    private var cachedLayoutAttributes: List<UICollectionViewLayoutAttributes> = listOf()

    private fun updateItemCounts() {
        val collectionView = this.collectionView
        _numSections = collectionView.dataSource?.numberOfSections(collectionView)
        if (_numSections <= 0) {
            _numItems = 0
            _sectionItemCounts = listOf()
            itemCountsAreValid = true
            return
        }
        _numItems = 0
        val sectionItemCounts = mutableListOf<Int>()
        for (i in 0 until _numSections) {
            val cellCount = collectionView.dataSource?.numberOfItemsInSection(collectionView, i)
            sectionItemCounts.add(cellCount)
            _numItems += cellCount
        }
        this._sectionItemCounts = sectionItemCounts.toList()
        this.itemCountsAreValid = true
    }

    private fun validateItemCounts() {
        if (!this.itemCountsAreValid) {
            this.updateItemCounts()
        }
    }

    private fun prepareToLoadData() {
        if (!this.layoutIsPrepared) {
            this.layout.prepareLayout()
            this.contentSize = this.layout.collectionViewContentSize()
            this.layoutIsPrepared = true
        }
    }

}